/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

import java.io.*;

/**
 * Class that handles loading of plugin files
 *
 * @author aschauer
 */
public class PluginClassLoader extends ClassLoader {

    File directory;

    /**
     * Create a new plugin loader or the given directory
     *
     * @param dir Directory to be parsed
     */
    public PluginClassLoader(File dir) {
        directory = dir;
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    @Override
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            Class c = findLoadedClass(name);

            if (c == null) {
                try {
                    c = findSystemClass(name);
                } catch (Exception ex) {
                }
            }

            if (c == null) {
                String filename = name.replace('.', File.separatorChar) + ".class";
                File f = new File(directory, filename);

                int length = (int) f.length();
                byte[] classbytes = new byte[length];
                try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
                    in.readFully(classbytes);
                }

                c = defineClass(name, classbytes, 0, length);
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;

        } catch (IOException | ClassFormatError ex) {
            throw new ClassNotFoundException(ex.toString());
        }
    }
}
