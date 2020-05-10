package dev.tricht.lunaris.com.pathofexile.itemtransformer;

import dev.tricht.lunaris.com.pathofexile.middleware.TradeMiddleware;
import dev.tricht.lunaris.com.pathofexile.request.Query;
import dev.tricht.lunaris.item.Item;
import dev.tricht.lunaris.item.types.EquipmentItem;
import dev.tricht.lunaris.item.types.MapItem;
import dev.tricht.lunaris.item.types.UnknownItem;
import dev.tricht.lunaris.item.types.WeaponItem;

import java.util.ArrayList;

public class ItemTransformer {

    private static ArrayList<TradeMiddleware> middleware = null;

    public static void setMiddleware(ArrayList<TradeMiddleware> middleware) {
        ItemTransformer.middleware = middleware;
    }

    public static Query createQuery(Item item) {
        if (item.getType() instanceof UnknownItem) {
            return null;
        }

        Query searchQuery = new Query();

        // Set search term (the "main" search bar containing item names)
        SearchTermSetter.set(item, searchQuery);

        // Set stat filters (where you input affixes, implicits, etc)
        // Only for equipment
        if (item.getType() instanceof EquipmentItem || item.getType() instanceof WeaponItem) {
            StatFilterSetter.set(item, searchQuery);
        }

        // Set misc filters like gem lvl, corrupted, etc
        MiscFilterSetter.set(item, searchQuery);

        // Set map tiers, influence etc
        if (item.getType() instanceof MapItem) {
            MapFilterSetter.set(item, searchQuery);
        }

        TypeFilterSetter.set(item, searchQuery);

        if (middleware != null) {
            for (TradeMiddleware middleware : middleware) {
                middleware.handle(item, searchQuery);
            }
        }

        return searchQuery;
    }
}
