package dev.tricht.poe.assistant.listeners;

import dev.tricht.poe.assistant.WindowsAPI;
import dev.tricht.poe.assistant.elements.*;
import dev.tricht.poe.assistant.item.Item;
import dev.tricht.poe.assistant.item.ItemGrabber;
import dev.tricht.poe.assistant.tooltip.TooltipCreator;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class ItemPriceListener implements NativeKeyListener, NativeMouseInputListener {

    private ItemGrabber itemGrabber;
    private Point position;

    public ItemPriceListener(ItemGrabber itemGrabber) {
        this.itemGrabber = itemGrabber;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        if (!WindowsAPI.isPoeActive()) {
            return;
        }
        if (event.getKeyCode() == NativeKeyEvent.VC_D && event.getModifiers() == NativeInputEvent.ALT_L_MASK) {
            try {
                Item item = this.itemGrabber.grab();

                Map<Element, int[]> elements = Map.ofEntries(
                        new AbstractMap.SimpleEntry<Element, int[]>(new Icon(item, 48), new int[]{0, 0}),
                        new AbstractMap.SimpleEntry<Element, int[]>(new ItemName(item,48 + Icon.PADDING), new int[]{1, 0}),
                        new AbstractMap.SimpleEntry<Element, int[]>(new Price(item), new int[]{1, 1}),
                        new AbstractMap.SimpleEntry<Element, int[]>(new Source("poe.ninja"), new int[]{1, 2})
                );

                TooltipCreator.create(position, elements);

            } catch (IOException | UnsupportedFlavorException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent event) {
        position = event.getPoint();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent event) {
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent event) {
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent event) {
        TooltipCreator.destroy();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent event) {
    }
}