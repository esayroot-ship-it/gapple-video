package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface favoriteService {
    R getFavoritesByUserId(Long userId);

    R addFavorite(Long userId, Long videoId);

    R deleteFavorite(Long favoriteId);
}
