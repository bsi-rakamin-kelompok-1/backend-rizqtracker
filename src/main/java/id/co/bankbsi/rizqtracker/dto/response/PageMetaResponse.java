package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PageMetaResponse {
    private long total;
    private int totalPages;
    private int currentPage;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;

    public static PageMetaResponse from(Page<?> page) {
        PageMetaResponse meta = new PageMetaResponse();
        meta.setTotal(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(page.getNumber() + 1); // Make 1-based for API consumers
        meta.setSize(page.getSize());
        meta.setHasNext(page.hasNext());
        meta.setHasPrevious(page.hasPrevious());

        return meta;
    }
}
