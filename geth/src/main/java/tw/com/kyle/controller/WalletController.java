package tw.com.kyle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;
import tw.com.kyle.dto.RestApiOneResponse;
import tw.com.kyle.service.WalletService;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@CommonsLog
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "錢包 API", description = "Wallet API")
public class WalletController extends BaseController {

    private final WalletService walletService;

    @Operation(summary = "建立錢包")
    @PostMapping(value = "/client/wallet")
    public RestApiOneResponse<CreateWalletRespDto> createWallet(@RequestBody @Valid CreateWalletReqDto createWalletReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(walletService.createWallet(createWalletReqDto, authentication));
    }

}
