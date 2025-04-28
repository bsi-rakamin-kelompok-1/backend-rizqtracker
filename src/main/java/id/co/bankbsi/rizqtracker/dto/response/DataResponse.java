package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse<T> extends BaseResponse {
    private T data;

    public DataResponse(boolean success, String message, T data) {
        super(success, message);
        this.data = data;
    }
}