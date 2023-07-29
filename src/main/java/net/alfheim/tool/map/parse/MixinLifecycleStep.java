package net.alfheim.tool.map.parse;

import net.alfheim.tool.map.api.mixin.MixinConfiguration;
import net.alfheim.tool.map.api.mixin.MixinEntry;
import net.alfheim.tool.map.exception.BuildProjectException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class MixinLifecycleStep implements LifecycleStep<Void> {
    public static final String MIXIN_CONFIG_FILE_SUFFIX = ".mixins.json";
    private Collection<MixinConfiguration> configurations;


    @Override
    public void transform(RoundEnvironment roundEnv) {
        final List<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MixinConfiguration.class)
                .stream()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .toList();

        this.configurations = new LinkedList<>();

        if(elements.isEmpty())
            return;


        this.configurations.addAll(
                elements.stream()
                        .map(element -> element.getAnnotation(MixinConfiguration.class))
                        .toList()
        );
    }

    @Override
    public Void process(ProcessingEnvironment processingEnv) throws BuildProjectException {
        if(this.configurations.isEmpty())
            return null;

        final Collection<String> mixinFiles = new LinkedList<>();
        final Map<String, Integer> injectors = this.generateInjectors();

        int index = 0;
        for(MixinConfiguration mixinConfiguration : this.configurations) {
            final Map<String, Object> fields = new HashMap<>();
            fields.put("required", mixinConfiguration.required());
            fields.put("minVersion", mixinConfiguration.minVersion());
            fields.put("package", mixinConfiguration.packagePath());
            fields.put("compatibilityLevel", mixinConfiguration.compatibilityLevel());
            fields.putAll(this.generateOrderedMixinCollection(mixinConfiguration));
            fields.put("injectors", injectors);

            final StringBuilder fileName = new StringBuilder();

            if(mixinConfiguration.fileBaseName().isEmpty()) {
                fileName.append("maptool").append(index);

                index += 1;
            } else {
                fileName.append(mixinConfiguration.fileBaseName());
            }

            fileName.append(MIXIN_CONFIG_FILE_SUFFIX);

            try {
                final FileObject config = processingEnv.getFiler()
                        .createResource(StandardLocation.CLASS_OUTPUT, "", fileName.toString());

                try(final Writer writer = config.openWriter()) {
                    JSONObject.writeJSONString(fields, writer);

                    mixinFiles.add(fileName.toString());
                } catch(Exception exception) {
                    throw new BuildProjectException("Couldn't write into mixin config file!", exception);
                }


            } catch (IOException exception) {
                throw new BuildProjectException("Couldn't create a new mixin config file!", exception);
            }
        }

        this.registerMixinFile(processingEnv, mixinFiles);

        return null;
    }


    private void registerMixinFile(ProcessingEnvironment processingEnv, Collection<String> mixinFiles) throws BuildProjectException {
        final File fabricConfig = this.openFabricJsonFile(processingEnv);
        try(final Reader reader = new FileReader(fabricConfig)) {
            final JSONParser parser = new JSONParser();

            final JSONObject object = (JSONObject) parser.parse(reader);
            final JSONArray mixins = (JSONArray) object.get("mixins");

            mixinFiles.stream()
                    .filter(mixinFile -> !mixins.contains(mixinFile))
                    .forEach(mixins::add);

            reader.close();

            try(final Writer writer = new FileWriter(fabricConfig)) {
                JSONObject.writeJSONString(object, writer);
            }
        } catch (Exception exception) {
            throw new BuildProjectException("Couldn't parse fabric config file!", exception);
        }
    }

    private File openFabricJsonFile(ProcessingEnvironment processingEnv) {
        try {
            final FileObject currentSourceSetFolder = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", "randomname.txt");

            final Path path = Path.of(currentSourceSetFolder.toUri()
                    .resolve("../")
                    .resolve("main/")
                    .resolve(FabricLifecycleStep.FABRIC_CONFIG_FILE)
            );
            final File file = new File(path.toString());

            currentSourceSetFolder.delete();

            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Collection<String>> generateOrderedMixinCollection(MixinConfiguration mixinConfiguration) {
        final Map<String, Collection<String>> mixins = new HashMap<>();

        final Collection<String> bothSide = new LinkedList<>();
        final Collection<String> clientSide = new LinkedList<>();
        final Collection<String> serverSide = new LinkedList<>();
        for (MixinEntry mixinEntry : mixinConfiguration.mixinEntries()) {
            switch(mixinEntry.side()) {
                case BOTH -> bothSide.add(mixinEntry.className());
                case CLIENT -> clientSide.add(mixinEntry.className());
                case SERVER -> serverSide.add(mixinEntry.className());
            }
        }

        if(!bothSide.isEmpty())
            mixins.put("mixins", bothSide);

        if(!clientSide.isEmpty())
            mixins.put("client", clientSide);

        if(!serverSide.isEmpty())
            mixins.put("server", serverSide);

        return mixins;
    }

    private Map<String, Integer> generateInjectors() {
        final Map<String, Integer> injectors = new HashMap<>();
        injectors.put("defaultRequire", 1);

        return injectors;
    }
}
