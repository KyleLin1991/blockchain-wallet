package tw.com.blockchain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.blockchain.service.CommonService;

import java.util.List;
import java.util.Map;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@CommonsLog
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "工具類 API", description = "Common API")
public class CommonController {

    private final CommonService commonService;

    @Operation(summary = "取得所有enum")
    @GetMapping(value = "/pub/enums")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getEnums() {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(commonService.getAllEnums());
    }
}
