package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecentAccountsResponse extends BaseResponse {
    private List<UserAccountResponse.Detail> data;
}