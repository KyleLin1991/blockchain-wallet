package tw.com.kyle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.req.WithdrawReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;
import tw.com.kyle.controller.resp.WithdrawRespDto;
import tw.com.kyle.dto.RestApiOneResponse;
import tw.com.kyle.service.WalletService;
import tw.com.kyle.service.WithdrawService;

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
    private final WithdrawService withdrawService;

    @Operation(summary = "建立錢包")
    @PostMapping(value = "/client/wallet")
    public RestApiOneResponse<CreateWalletRespDto> createWallet(@RequestBody @Valid CreateWalletReqDto createWalletReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(walletService.createWallet(createWalletReqDto, authentication));
    }

    @Operation(summary = "提領")
    @PostMapping(value = "/client/withdraw")
    public RestApiOneResponse<WithdrawRespDto> withdraw(@RequestBody @Valid WithdrawReqDto withdrawReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(withdrawService.withdraw(withdrawReqDto, authentication));
    }

    @Operation(summary = "查詢錢包餘額")
    @GetMapping(value = "/client/{address}/balance")
    public RestApiOneResponse<String> getBalance(@PathVariable String address) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(walletService.getBalance(address));
    }

    @Operation(summary = "測試Websocket斷線")
    @GetMapping(value = "/client/close")
    public RestApiOneResponse<String> testConnectClose() {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }
        walletService.simulateDisconnect();

        return super.doGetOneResult("成功");
    }

}
