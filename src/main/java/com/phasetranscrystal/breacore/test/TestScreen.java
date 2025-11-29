package com.phasetranscrystal.breacore.test;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import icyllis.modernui.TestFragment;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.mc.MuiScreen;
import icyllis.modernui.mc.ScreenCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestScreen extends Screen implements MuiScreen {

    protected TestScreen(Component title) {
        super(title);
    }

    @Override
    public @NotNull Screen self() {
        return this;
    }

    @Override
    public @NotNull Fragment getFragment() {
        return new TestFragment.FragmentA();
    }

    @Override
    public @Nullable ScreenCallback getCallback() {
        return null;
    }

    @Override
    public @Nullable Screen getPreviousScreen() {
        return null;
    }

    @Override
    public boolean isMenuScreen() {
        return false;
    }

    @Override
    public void onBackPressed() {}
}
