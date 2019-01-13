package com.team2073.common.util;

import com.team2073.common.assertion.Assert;
import com.team2073.common.exception.EagleEx;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.team2073.common.util.JvmUtil.RuntimeEnvironment.*;

/**
 * WARNING: This class needs a LOT of work and a lot of testing in different scenarios.
 * Please let me know when things don't work so I can fix them :)
 *
 * @author Preston Briggs
 */
public abstract class JvmUtil {
    
    
    
    
    
    // WARNING: This class needs a LOT of work and a lot of testing in different scenarios.
    // Please let me know when things don't work so I can fix them :)
    // ~Preston
    
    
    
    
    

    private static final String JAR_URI_PREFIX = "jar:file:";

    public enum RuntimeEnvironment {
        JAR,
        CLASS
    }
    
    /**
     * See {@link #runtimeEnv(Class)} for details.
     */
    public static boolean envIsJar(Class<?> clazz) {
        return runtimeEnv(clazz) == JAR;
    }
    
    /**
     * See {@link #runtimeEnv(Class)} for details.
     */
    public static boolean envIsJar(URL url) {
        return runtimeEnv(url) == JAR;
    }

    /**
     * Returns whether the current JVM is running from a jar or from .class files (as is the case when running from an
     * IDE for example).<br/>
     * <br/>
     * One example use of this is to determine how to copy files from the classpath to an external directory since special
     * handling is required if classpath files exist inside a jar.
     *
     * @param clazz A class in the same jar (or project) as the requested files, usually just <b>this.getClass()</b>
     * @return Whether the current JVM is running from a jar or from .class files
     */
    public static RuntimeEnvironment runtimeEnv(Class<?> clazz){
        URL url = classAsResourceUrl(clazz);
        return runtimeEnv(url);
    }
    
    public static RuntimeEnvironment runtimeEnv(URL url){
        String protocol = url.getProtocol();

        if (protocol == null)
            throw EagleEx.newInternal("Could not determine protocol for url [{}].", url);

        if (protocol.equals("jar")) {

            if (url.toString().contains("!"))
                return JAR;
            else
                throw EagleEx.newInternal("We checked that the URL's protocol was 'jar' but it still doesn't " +
                        "contain a '!'. URL protocol: [{}], URL: [{}].", protocol, url);

        } else if (protocol.equals("file"))
            return CLASS;
        else
            throw EagleEx.newInternal("Unknown protocol [{}] for url [{}].", protocol, url);
    }

    /** See {@link #copyResourcesToDirectory(Object, String, File, boolean)} */
    public static File copyResourcesToDirectory(Object classObject, String sourcePath, File destDir) throws IOException {
        return copyResourcesToDirectory(classObject, sourcePath, destDir, false);
    }

    /**
     * Copies files from the classpath to the destDir. This works regardless of whether running from
     * a jar or running from .class files.
     *
     * @param classObject A object whose class is in the same jar (or project) as the requested files (usually just <b>this</b>)
     * @param sourcePath The path to the directory or file on the classpath to copy from
     * @param destDir The directory or file to copy to
     * @param failIfSourceDirMissing Whether an exception should be thrown if the sourcePath directory does not exist (default is false)
     * @throws IOException If the file or directory given by sourcePath doesn't exist
     * @throws IOException If source or destination is invalid
     * @throws IOException If an IO error occurs during copying
     */
    public static File copyResourcesToDirectory(Object classObject, String sourcePath, File destDir, boolean failIfSourceDirMissing) throws IOException {
        Assert.assertNotNull(classObject, "classObject");
        return copyResourcesToDirectory(classObject.getClass(), sourcePath, destDir, failIfSourceDirMissing);
    }

    /** See {@link #copyResourcesToDirectory(Object, String, File, boolean)} */
    public static File copyResourcesToDirectory(Class<?> clazz, String sourcePath, File destDir) throws IOException {
        return copyResourcesToDirectory(clazz, sourcePath, destDir, false);
    }

    /** See {@link #copyResourcesToDirectory(Object, String, File, boolean)} */
    public static File copyResourcesToDirectory(Class<?> clazz, String sourcePath, File destDir, boolean failIfSourceDirMissing) throws IOException {

        if (envIsJar(clazz)) {
            JarFile jarFile = jarForClass(clazz);
            copyResourcesFromJarToDirectory(jarFile, sourcePath, destDir);
        } else {
            copyResourcesFromClasspathToDirectory(sourcePath, destDir, clazz, failIfSourceDirMissing);
        }
        return new File(destDir, sourcePath);
    }

    /**
     * Copies a directory or file from the classpath to an external directory.
     */
    private static void copyResourcesFromClasspathToDirectory(String sourcePath, File destDir, Class<?> clazz, boolean failIfSourceDirMissing) throws IOException {
    
        URL resource = clazz.getClassLoader().getResource(sourcePath);
        
        if (resource == null)
            resource = clazz.getResource("/" + sourcePath);
        
        if (resource == null) {
            if (failIfSourceDirMissing)
                throw Ex.io("No file found for sourcePath [{}].", sourcePath);

            new File(destDir, sourcePath).mkdirs();
            return;
        } else {
    
            if (envIsJar(resource)) {
                copyResourcesFromJarToDirectory(jarForUrl(resource), sourcePath, destDir);
                return;
            } else {
                File confFile = new File(resource.getFile());
                if (confFile.isDirectory())
                    FileUtils.copyDirectory(confFile, destDir);
                else
                    FileUtils.copyFile(confFile, new File(destDir, confFile.getName()));
            }
        }
    }

    /**
     * Copies a directory or file from a jar to an external directory.
     */
    private static void copyResourcesFromJarToDirectory(JarFile fromJar, String sourcePath, File destDir) throws IOException {

        for (Enumeration<JarEntry> entries = fromJar.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(sourcePath + "/") && !entry.isDirectory()) {
                File dest = new File(destDir + "/" + entry.getName().substring(sourcePath.length() + 1));
                File parent = dest.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                FileOutputStream out = new FileOutputStream(dest);
                InputStream in = fromJar.getInputStream(entry);

                try {
                    byte[] buffer = new byte[8 * 1024];

                    int s = 0;
                    while ((s = in.read(buffer)) > 0) {
                        out.write(buffer, 0, s);
                    }
                } catch (IOException e) {
                    throw new IOException("Could not copy asset from jar file", e);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ignored) {}
                    try {
                        out.close();
                    } catch (IOException ignored) {}
                }
            }
        }
    }
    
    /**
     * Returns the jar file used to load class.
     *
     * @param clazz A class in the same jar (or project), usually just <b>this.getClass()</b>
     * @return The jar file used to load class
     */
    public static JarFile jarForClass(Class<?> clazz) {
        URL url = classAsResourceUrl(clazz);
        return jarForUrl(url);
    }

    private static JarFile jarForUrl(URL url) {

        if (!envIsJar(url)) {
            throw Ex.illegalState("Cannot determine jar file when not running from a JAR. "
                    + "Is application running from an IDE or .class files? Use JvmUtil.envIsJar() method before calling this method.");
        }

        String urlString = url.toString();
        int bang = urlString.indexOf("!");
        try {
            return new JarFile(urlString.substring(JAR_URI_PREFIX.length(), bang));
        } catch (IOException e) {
            throw new IllegalStateException("Error loading jar file.", e);
        }
    }

    private static String classAsResource(Class<?> clazz) {
        return clazz.getSimpleName() + ".class";
    }

    private static URL classAsResourceUrl(Class<?> clazz) {
        String classResourceName = classAsResource(clazz);
        return clazz.getResource(classResourceName);
    }

    private static String classAsResourceAbsolute(Class<?> clazz) {
        return classAsResourceUrl(clazz).toString();
    }
}
