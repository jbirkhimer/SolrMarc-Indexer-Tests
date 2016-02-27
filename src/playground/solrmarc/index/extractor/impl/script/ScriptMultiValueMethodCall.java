package playground.solrmarc.index.extractor.impl.script;

import bsh.BshMethod;
import bsh.Interpreter;
import playground.solrmarc.index.extractor.methodcall.AbstractMethodCall;

import java.util.Collection;

public class ScriptMultiValueMethodCall extends AbstractMethodCall<Collection<String>> {
    private final Interpreter interpreter;
    private final BshMethod method;

    public ScriptMultiValueMethodCall(final Interpreter interpreter, final BshMethod method, final String scriptFileName) {
        super(scriptFileName, method.getName());
        this.interpreter = interpreter;
        this.method = method;

        if (this.interpreter == null) {
            throw new NullPointerException("Interpreter is null.");
        } else if (!String.class.isAssignableFrom(this.method.getReturnType())) {
            throw new IllegalArgumentException("The method's return type has to be assignable to String:\nScript:  " + scriptFileName + "\nMethod: " + method.toString());
        }
    }

    @Override
    public Collection<String> invoke(final Object[] parameters) throws Exception {
        return (Collection<String>) method.invoke(parameters, interpreter);
    }
}
