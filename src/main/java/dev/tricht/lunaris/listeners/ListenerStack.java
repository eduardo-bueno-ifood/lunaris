package dev.tricht.lunaris.listeners;

import dev.tricht.lunaris.com.pathofexile.PathOfExileAPI;
import dev.tricht.lunaris.info.poeprices.PoePricesAPI;
import dev.tricht.lunaris.item.ItemGrabber;
import dev.tricht.lunaris.util.PropertiesManager;
import lombok.extern.slf4j.Slf4j;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class ListenerStack {

    private ItemPriceListener priceListener = null;

    public void startListeners(ItemGrabber itemGrabber, Robot robot, PathOfExileAPI pathOfExileAPI, PoePricesAPI poePricesAPI) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            log.error("Failed to start native hooks", e);
            System.exit(1);
            return;
        }

        GlobalScreen.setEventDispatcher(new VoidDispatchService());

        HotKeyHandler handler = new HotKeyHandler();
        GlobalScreen.addNativeKeyListener(handler);
        GlobalScreen.addNativeMouseMotionListener(handler);
        GlobalScreen.addNativeMouseListener(handler);
        GlobalScreen.addNativeMouseWheelListener(handler);

        PropertiesManager.addPropertyListener("keybinds(.*)", () -> {
            log.debug("Restarting listeners after keybind change");
            handler.removeListeners();
            if (priceListener != null) {
                GlobalScreen.removeNativeMouseListener(priceListener);
            }

            startListeners(handler, itemGrabber, robot, pathOfExileAPI, poePricesAPI);
        });

        startListeners(handler, itemGrabber, robot, pathOfExileAPI, poePricesAPI);
    }

    private void startListeners(HotKeyHandler handler, ItemGrabber itemGrabber, Robot robot, PathOfExileAPI pathOfExileAPI,
                                PoePricesAPI poePricesAPI) {
        ArrayList<KeyCombo> combos = new ArrayList<>();
        for(Map.Entry<String, String> property : PropertiesManager.getAllPropertiesMatching("keybinds(.*)").entrySet()) {
            combos.add(new KeyCombo(property.getValue()));
        }
        handler.setRespondTo(combos);

        ItemInfoListener infoListener = new ItemInfoListener(new KeyCombo(PropertiesManager.getProperty("keybinds.item_info")));
        infoListener.addInfoListener(new MapInfoListener());
        infoListener.addInfoListener(new CurrencyStackListener());
        infoListener.addInfoListener(new WeaponInfoListener());

        ClipboardListenerStack clipboardListenerStack = new ClipboardListenerStack(itemGrabber, robot);
        clipboardListenerStack.addListener(infoListener);
        clipboardListenerStack.addListener(new WikiListener(new KeyCombo(PropertiesManager.getProperty("keybinds.wiki"))));

        priceListener = new ItemPriceListener(
                new KeyCombo(PropertiesManager.getProperty("keybinds.price_check")),
                new KeyCombo(PropertiesManager.getProperty("keybinds.search_trade")),
                pathOfExileAPI,
                poePricesAPI
        );
        clipboardListenerStack.addListener(priceListener);
        if (priceListener != null) {
            GlobalScreen.removeNativeMouseListener(priceListener);
        }
        GlobalScreen.addNativeMouseListener(priceListener);


        handler.addListener(new HideoutListener(new KeyCombo(PropertiesManager.getProperty("keybinds.hideout" )), robot));
        handler.addListener(new KickSelfListener(new KeyCombo(PropertiesManager.getProperty("keybinds.kick")), robot));
        handler.addListener(new InviteLastWhisperListener(new KeyCombo(PropertiesManager.getProperty("keybinds.invite_last_whisper")), robot));

        if(PropertiesManager.getProperty("keybinds.enable_stash_scroll", "1").equals("1")) {
            handler.addListener(new MouseScrollCombo(NativeInputEvent.CTRL_L_MASK), new StashScrollListener(robot));
        }

        handler.addListener(clipboardListenerStack);
    }
}
