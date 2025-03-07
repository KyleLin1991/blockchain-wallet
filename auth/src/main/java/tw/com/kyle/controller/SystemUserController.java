package tw.com.kyle.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@CommonsLog
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "後台使用者 API", description = "System User API")
public class SystemUserController {


}
