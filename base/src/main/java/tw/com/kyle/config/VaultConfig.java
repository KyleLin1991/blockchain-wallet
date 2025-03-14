package tw.com.kyle.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import tw.com.kyle.misc.VaultProperties;

/**
 * @author Kyle
 * @since 2025/3/13
 */
@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

    @Autowired
    private VaultProperties vaultProperties;

    @NotNull
    @Override
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint endpoint = VaultEndpoint.create(vaultProperties.getHost(), Integer.parseInt(vaultProperties.getPort()));
        endpoint.setScheme(vaultProperties.getScheme());

        return endpoint;
    }

    @NotNull
    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(vaultProperties.getToken());
    }
}
