package net.alfheim.tool.map.parse;

import net.alfheim.tool.map.api.Side;
import net.alfheim.tool.map.api.fabric.Entrypoint;
import net.alfheim.tool.map.api.fabric.FabricMod;
import net.alfheim.tool.map.exception.BuildProjectException;
import net.alfheim.tool.map.exception.ParseProjectException;
import org.json.simple.JSONObject;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.BiConsumer;

public class FabricLifecycleStep implements LifecycleStep<FileObject> {
    public static final String FABRIC_CONFIG_FILE = "fabric.mod.json";
    private String className;
    private FabricMod annotation;

    @Override
    public void transform(RoundEnvironment roundEnv) throws ParseProjectException {
        final List<? extends Element> elements = roundEnv.getElementsAnnotatedWith(FabricMod.class)
                .stream()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .toList();

        if(elements.size() > 1) {
            throw new ParseProjectException("Found multiple FabricMod annotations, conflict!");
        }

        if(elements.size() == 1) {
            this.annotation = elements.get(0).getAnnotation(FabricMod.class);
            this.className = elements.get(0).toString();
        }
    }

    @Override
    public FileObject process(ProcessingEnvironment processingEnv) throws BuildProjectException {
        if(this.annotation == null)
            return null;

        final Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("schemaVersion", this.annotation.schemaVersion().getValue());
        fields.put("id", this.annotation.id());
        fields.put("version", this.annotation.version());
        fields.put("name", this.annotation.name());
        fields.put("description", this.annotation.description());
        fields.put("authors", Arrays.stream(this.annotation.authors()).toList());
        fields.put("icon", this.annotation.icon());
        fields.put("license", this.annotation.license());
        fields.put("environment", this.annotation.environment());
        fields.put("entrypoints", this.generateEntryPointsDictionary());
        fields.put("mixins", Collections.emptyList());

        this.generateRelationDictionary().forEach(entry -> fields.put(entry.getKey(), entry.getValue()));

        try {
            final FileObject config = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", FABRIC_CONFIG_FILE);

            try(final Writer writer = config.openWriter()) {
                JSONObject.writeJSONString(fields, writer);

                writer.close();

                return config;
            } catch(Exception exception) {
                throw new BuildProjectException("Couldn't write into fabric config file!", exception);
            }


        } catch (IOException exception) {
            throw new BuildProjectException("Couldn't create a new fabric config file!", exception);
        }
    }

    private Map<String, Collection<String>> generateEntryPointsDictionary() {
        final Map<String, Collection<String>> entryPoints = new HashMap<>();

        final Collection<String> clientEntryPoints = new ArrayList<>();
        final Collection<String> serverEntryPoints = new ArrayList<>();
        final Collection<String> mainEntryPoints = new ArrayList<>();

        final BiConsumer<Side, String> addEntry = (side, path) -> {
            switch (side) {
                case BOTH -> mainEntryPoints.add(path);
                case CLIENT -> clientEntryPoints.add(path);
                case SERVER -> serverEntryPoints.add(path);
            }
        };

        addEntry.accept(this.annotation.side(), this.className);
        for(Entrypoint entrypoint : this.annotation.entryPoints()) {
            addEntry.accept(entrypoint.side(), entrypoint.path());
        }

        entryPoints.put("client", clientEntryPoints);
        entryPoints.put("server", serverEntryPoints);
        entryPoints.put("main", mainEntryPoints);

        return entryPoints;
    }

    private Collection<Map.Entry<String, Map<String, String>>> generateRelationDictionary() {
        final Collection<Map.Entry<String, Map<String, String>>> relations = new ArrayList<>();

        final Map<String, String> depends = new LinkedHashMap<>();
        depends.put("fabricloader", ">=0.14.21");
        depends.put("fabric-api", "*");
        depends.put("minecraft", "~1.20");
        depends.put("java", ">=17");

        relations.add(new AbstractMap.SimpleEntry<>("depends", depends));

        return relations;
    }
}
