package net.alfheim.tool.map.parse;

import net.alfheim.tool.map.api.command.Command;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class CommandLifecycleStep implements LifecycleStep<Object> {
    @Override
    public void transform(RoundEnvironment roundEnv) {
        for(Element element : roundEnv.getElementsAnnotatedWith(Command.class)) {
            if(element.getKind() == ElementKind.METHOD) {
                // TODO To complete
            }
        }
    }

    @Override
    public Object process(ProcessingEnvironment processingEnv) {
        return null;
    }
}
