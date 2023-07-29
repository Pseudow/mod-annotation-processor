package net.alfheim.tool.map;

import net.alfheim.tool.map.exception.BuildProjectException;
import net.alfheim.tool.map.exception.ParseProjectException;
import net.alfheim.tool.map.parse.FabricLifecycleStep;
import net.alfheim.tool.map.parse.LifecycleStep;
import net.alfheim.tool.map.parse.MixinLifecycleStep;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * This class is the main core of the Mod Annotation Processor,
 * it transforms annotations into what they should represent.
 * All those actions are performed in compile time.
 */
@SupportedAnnotationTypes(value = {
        "net.alfheim.tool.map.api.mixin.MixinConfiguration",
        "net.alfheim.tool.map.api.fabric.FabricMod"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ModAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final FabricLifecycleStep fabricLifecycleStep = new FabricLifecycleStep();
        this.buildAndRunStep(fabricLifecycleStep, roundEnv);

        final MixinLifecycleStep mixinLifecycleStep = new MixinLifecycleStep();
        this.buildAndRunStep(mixinLifecycleStep, roundEnv);

        return true;
    }

    private <T> void buildAndRunStep(LifecycleStep<T> step, RoundEnvironment roundEnv) {
        try {
            step.transform(roundEnv);
            step.process(this.processingEnv);
        } catch (ParseProjectException | BuildProjectException exception) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, exception.getLocalizedMessage());
            throw new RuntimeException(exception);
        }
    }
}