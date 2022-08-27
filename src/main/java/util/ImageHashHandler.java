package util;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ImageHashHandler {
    private static double powerOfComparing = 0.2;
    private static final HashingAlgorithm hasher = new PerceptiveHash(32);
    public static final Logger log = LoggerFactory.getLogger(PlagiarismHandler.class);



    public static boolean comparePictureHash(Hash hash1, Hash hash2) {
        log.info("comparing pictures' hashes");

        return (hash1.normalizedHammingDistance(hash2)) < powerOfComparing;
    }

    public static Hash getImageHash(TdApi.File image) throws IOException {
        log.info("Getting hash from the picture");
        File file = new File(image.local.path);
        return hasher.hash(file);
    }

}
