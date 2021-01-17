package restdoc.client.web.controller;

import org.springframework.web.bind.annotation.*;
import restdoc.client.web.model.dto.CreateOrderDto;
import restdoc.client.web.model.dto.RequestParamDto;
import restdoc.client.web.model.dto.UploadFileDto;
import restdoc.client.web.model.vo.NoDefaultConstructorVo;
import restdoc.client.web.model.vo.Response;

import javax.servlet.http.HttpServletRequest;

@RestController
public class OrderController {

    // /test/123;q=123
    @PostMapping(
            value = "/mch/order/{oid}",
            name = "createOrder",
            params = {"myParam!=myValue"},
            headers = {"restdoc_console_access_token"},
            consumes = {"text/plain", "application/*"},
            produces = {"text/plain", "application/*"}
    )
    public Response createOrder(@PathVariable String oid,
                                @RequestParam String key,
                                @RequestBody CreateOrderDto dto,
                                @RequestHeader(name = "Content-Type") String ct,
                                @MatrixVariable(name = "a", pathVar = "oid") int a,
                                @CookieValue(required = false, name = "restdoc_console_access_token") String cookie,
                                HttpServletRequest request
    ) {

        String header = request.getHeader("Content-Type");

        return Response.ok();
    }

    @PostMapping("/mch/order/upload")
    public Response uploadFile(@RequestPart UploadFileDto dto) {
        return Response.ok();
    }

    @GetMapping("/NoDefaultConstructorVo")
    public NoDefaultConstructorVo echoNoDefaultConstructorVo() {
        return new NoDefaultConstructorVo("field");
    }

    /**
     * buildRequestParamDto 错误示例
     *
     * @param dto
     * @return
     */
    @GetMapping("/buildRequestParamDto")
    @Deprecated
    public Response buildRequestParamDto(@RequestParam @RequestBody RequestParamDto dto) {
        System.err.println(dto.getName());
        return Response.ok();
    }


    /**
     * buildRequestParamDto 错误示例
     *
     * @param dto
     * @return
     */
    @GetMapping("/buildRequestParamDto1")
    @Deprecated
    public Response buildRequestParamDt1o(@RequestParam @RequestBody RequestParamDto dto) {
        System.err.println(dto.getName());
        return Response.ok();
    }
}
