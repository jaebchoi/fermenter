package org.technologybrewery.fermenter.mda.test.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;
import org.technologybrewery.fermenter.mda.element.Target;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Utility class that provides reading and retrieving profile.json and targets.json specific testing
 * to help ease creating cucumber unit test for fermenter resource generation.
 */
public class FermenterMDATestUtil {

    /**
     * Loads all Profiles and Targets Map on given profiles.json and target.json file.
     *
     * @param profilesFile
     *            {@link File} referencing profiles.json file desired to load.
     * @param targetsFile
     *            {@link File} referencing targets.json file desired to load.
     * @return {@link Pair} containing Profile Map and Targets Map, key being name of profile and target respectively.
     *         Left Pair is Profile Map and Right Pair is Target Map.
     * @throws IOException
     */
    public static Pair<Map<String, ExpandedProfile>, Map<String, Target>> readProfilesAndTargetFile(File profilesFile, File targetsFile) throws IOException {
        Map<String, ExpandedProfile> profilesMap;
        Map<String, Target> targetsMap;
        profilesMap = readProfilesFile(profilesFile);
        targetsMap = readTargetsFile(targetsFile);
        //This is needed to populate profiles references and targets references in ExpandedProfile object.
        for (ExpandedProfile p : profilesMap.values()) {
            p.dereference(profilesMap, targetsMap);
        }

        return Pair.of(profilesMap, targetsMap);
    }

    /**
     * Loads all Profiles Map on given profiles.json file.
     *
     * @param profilesFile
     *            {@link File} referencing profiles.json file desired to load.
     * @return {@link Map} Profile Map key being name of profile and value being ExpandedProfile object.
     * @throws IOException
     */
    public static Map<String, ExpandedProfile> readProfilesFile(File profilesFile) throws IOException {
        Map<String, ExpandedProfile> profilesMap = new HashMap<>();
        InputStream profileStream = new FileInputStream(profilesFile);
        profilesMap = GenerateSourcesHelper.loadProfiles(profileStream, profilesMap);

        return profilesMap;
    }

    /**
     * Loads all Targets Map on given target.json file.
     *
     * @param targetsFile
     *            {@link File} referencing targets.json file desired to load.
     * @return {@link Map} Target Map key being name of target and value being Target object.
     * @throws IOException
     */
    public static Map<String, Target> readTargetsFile(File targetsFile) throws IOException {
        Map<String, Target> targetsMap = new HashMap<>();
        InputStream targetStream = new FileInputStream(targetsFile);
        targetsMap = GenerateSourcesHelper.loadTargets(targetStream, targetsMap);

        return targetsMap;
    }

    /**
     * Returns missing target References on a given Profile and specified TargetReferences,
     *
     * @param profile
     *            {@link ExpandedProfile} referencing profile to inspect.
     * @param targetReferences
     *            {@link List} list of targetReference to search.
     * @return {@link List} Return list of missing targetReferences on a given profile.
     */
    public static List<String> retriveMissingTargets(ExpandedProfile profile, List<String> targetReferences)
    {
        List<String> profileTargetReferences = profile.getTargets().stream().map(Target::getName).collect(Collectors.toList());
        List<String> missingTargets = new ArrayList<>(CollectionUtils.removeAll(targetReferences, profileTargetReferences));
        return missingTargets;
    }

    /**
     * Returns missing profile References on a given Profile and specified profileReferences.
     *
     * @param profile
     *            {@link ExpandedProfile} referencing profile to inspect.
     * @param profileReferences
     *            {@link List} list of profileReferences to search.
     * @return {@link List} Return list of missing profileReferences on a given profile.
     */
    public static List<String> retrieveMissingProfiles(ExpandedProfile profile, List<String> profileReferences)
    {
        List<String> profileProfileReferences = profile.getReferencedProfiles().stream().map(ExpandedProfile::getName).collect(Collectors.toList());
        List<String> missingProfiles = new ArrayList<>(CollectionUtils.removeAll(profileReferences, profileProfileReferences));
        return missingProfiles;
    }

    /**
     * check if template file exist on a given target.
     *
     * @param target
     *            {@link Target} referencing target to inspect.
     * @param baseDirectory
     *            {@link String} Base Directory of template file.
     * @return boolean Return Whether template file on a given target exists.
     */
    public static boolean isTemplateFileExist(Target target, String baseDirectory){
        String fullTemplateFilePath = baseDirectory + target.getTemplateName();
        File templateFile = new File(fullTemplateFilePath);
        return templateFile.exists();
    }

    /**
     * check if Generator file class exist on Classpath.
     *
     * @param target
     *            {@link Target} referencing target to inspect.
     * @return boolean Return Whether generator class on a given target exists and loaded.
     */
    public static boolean isGeneratorOnClassPath(Target target) {
        try{
            Class.forName(target.getGenerator());
            return true;
        } catch (ClassNotFoundException e)
        {
            return false;
        }
    }
}
