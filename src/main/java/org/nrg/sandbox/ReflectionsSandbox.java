package org.nrg.sandbox;

import com.google.common.base.Predicate;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class ReflectionsSandbox {

    public static void main(String[] args) throws IOException {
        // This will get every single properties file on the classpath.
        final Reflections reflections1 = new Reflections(new ResourcesScanner());
        final Set<String> resources1 = reflections1.getResources(Pattern.compile(".*\\.properties"));
        System.out.println("Found " + resources1.size() + " resources with no package specified anywhere at all.");

        // This will get all of the properties files in this project, regardless of package.
        final Reflections reflections2 = new Reflections("", new ResourcesScanner());
        final Set<String> resources2 = reflections2.getResources(Pattern.compile(".*\\.properties"));
        System.out.println("Found " + resources2.size() + " resources using the prefix \"\"");

        // This will get any properties files (only one in this case) under the config package.
        final Reflections reflections3 = new Reflections("config", new ResourcesScanner());
        final Set<String> resources3 = reflections3.getResources(Pattern.compile(".*\\.properties"));
        System.out.println("Found " + resources3.size() + " resources using the package prefix \"config\"");

        // This will oddly get all properties files in this project, regardless of package, in spite of having the ClasspathHelper specifying the package.
        final Reflections reflections4 = new Reflections(ClasspathHelper.forPackage("config"), new ResourcesScanner());
        final Set<String> resources4 = reflections4.getResources(Pattern.compile(".*\\.properties"));
        System.out.println("Found " + resources4.size() + " resources using the package prefix \"config\" but with the ClasspathHelper instead of just a string in the constructor");

        // This one will find all properties in this project in the default package. It's almost what I want...
        final Reflections reflections5 = new Reflections(new ConfigurationBuilder().forPackages("").setScanners(new ResourcesScanner()).filterInputsBy(ROOT_PREDICATE));
        final Set<String> resources5 = reflections5.getResources(Pattern.compile(".*\\.properties"));
        System.out.println("Found " + resources5.size() + " matching resources out of " + ROOT_PREDICATE.getCount() + " candidates");
        ROOT_PREDICATE.resetCount();

        // This one will find nothing.
        final Reflections reflections6 = new Reflections(new ConfigurationBuilder().forPackages().setScanners(new ResourcesScanner()).filterInputsBy(ROOT_PREDICATE));
        final Set<String> resources6 = reflections6.getResources(Pattern.compile(".*\\.properties"));
        System.out.println("Found " + resources6.size() + " matching resources out of " + ROOT_PREDICATE.getCount() + " candidates");
    }

    static class MatchRootProperties implements Predicate<String> {
        @Override
        public boolean apply(final String s) {
            count++;
            return ROOT_PROPERTIES_FILE.matcher(s).matches();
        }
        
        public int getCount() {
            return count;
        }
        
        public void resetCount() {
            count = 0;
        }

        private int count = 0;
        private static final Pattern ROOT_PROPERTIES_FILE = Pattern.compile("^[^\\.]+\\.properties");
    }
    private static final MatchRootProperties ROOT_PREDICATE = new MatchRootProperties();
}
