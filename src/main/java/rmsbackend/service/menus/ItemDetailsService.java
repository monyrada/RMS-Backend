package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmsbackend.dto.menus.itemdetails.ItemDetailRequest;
import rmsbackend.dto.menus.itemdetails.ItemDetailResponse;
import rmsbackend.repository.menus.ItemDetailRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemDetailsService {

    private final ItemDetailRepository itemDetailRepository;

    public ItemDetailResponse createItemDetail(ItemDetailRequest request) {

        return null;
    }
}
