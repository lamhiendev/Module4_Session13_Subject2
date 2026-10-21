package demo.identityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private int status;

    public static ApiResponse success(String message, Object data){
        return new ApiResponse(true,message,data, HttpStatus.OK.value());
    }

    public static ApiResponse error(String message, HttpStatus status){
        return new ApiResponse(false, message,null,status.value());
    }
}
