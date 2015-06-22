package stringcheese;

import java.util.ArrayList;
import java.io.Writer;

public class ArrayListWriter extends Writer {

    public final ArrayList<CharSequence> content;

    public ArrayListWriter() {
        this.content = new ArrayList<CharSequence>();
    }

    public ArrayList getContent() {
        return this.content;
    }

    @Override
    public synchronized Writer append(char c) {
        this.content.add(String.valueOf(c));
        return this;
    }

    @Override
    public synchronized Writer append(CharSequence c) {
        this.content.add(c);
        return this;
    }

    @Override
    public Writer append(CharSequence c, int start, int end) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void close() {
        return;
    }

    @Override
    public void flush() {
        return;
    }

    @Override
    public synchronized void write(char[] cbuf) {
        this.content.add(new String(cbuf));
    }
   
    @Override
    public synchronized void write(char[] cbuf, int start, int end) {
        this.content.add(new String(cbuf, start, end));
    }

    @Override
    public void write(int c) {
        this.append((char) c);
    }

    @Override
    public synchronized void write(String s) {
        this.content.add(s);
    }

    @Override
    public void write(String s, int start, int end) {
        this.write(s.substring(start, end));
    }

}
