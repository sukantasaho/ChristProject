package com.christ.erp.services.security;

import com.christ.erp.services.common.AppProperties;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.vault.VaultUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class JWTPasswordEncoder implements PasswordEncoder {
    private final static String Secret;
    private final static String Iteration;
    private final static String KeyLength;

    static {
        //Secret = AppProperties.get("security.password.encoder.secret");
        //Iteration = AppProperties.get("security.password.encoder.iteration");
        //KeyLength = AppProperties.get("security.password.encoder.keylength");
        Secret = VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG,"security.password.encoder.secret").getValue();
        Iteration = VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG,"security.password.encoder.iteration").getValue();
        KeyLength = VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG,"security.password.encoder.keylength").getValue();

    }

    @Override
    public String encode(CharSequence cs) {
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")//cost is high. may use 256
                    .generateSecret(new PBEKeySpec(cs.toString().toCharArray(),
                            JWTPasswordEncoder.Secret.getBytes(),
                            Integer.parseInt(JWTPasswordEncoder.Iteration),
                            Integer.parseInt(JWTPasswordEncoder.KeyLength)))
                    .getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return encode(cs).equals(string);
    }
}
