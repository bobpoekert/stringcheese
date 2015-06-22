package stringcheese;

public class TemplateException extends Exception {


    public TemplateException(Throwable exc, long lineNumber, long charNumber, String source) {
        super(String.format("Error at line %d char %d in %s", lineNumber, charNumber, source), exc);
    }
    

}
