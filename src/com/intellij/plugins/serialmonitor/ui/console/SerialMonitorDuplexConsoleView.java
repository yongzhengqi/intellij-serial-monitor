package com.intellij.plugins.serialmonitor.ui.console;

import com.intellij.execution.console.DuplexConsoleView;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.project.DumbAware;
import icons.SerialMonitorIcons;
import org.jetbrains.annotations.NotNull;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorDuplexConsoleView extends DuplexConsoleView<ConsoleView, HexConsoleView> {

    private static final String STATE_STORAGE_KEY = "SerialMonitorDuplexConsoleViewState";

    @NotNull
    private final ConsoleView myPrimaryConsoleView;

    @NotNull
    private final HexConsoleView mySecondaryConsoleView;

    @NotNull
    private final AnAction mySwitchConsoleAction;

    public SerialMonitorDuplexConsoleView(@NotNull ConsoleView primaryConsoleView, @NotNull HexConsoleView secondaryConsoleView) {
        super(primaryConsoleView, secondaryConsoleView, STATE_STORAGE_KEY);

        mySwitchConsoleAction = createSwitchConsoleAction();

        // save references to use in overridden methods
        myPrimaryConsoleView = primaryConsoleView;
        mySecondaryConsoleView = secondaryConsoleView;
    }

    private AnAction createSwitchConsoleAction() {
        return new SwitchConsoleViewAction();
    }

    @NotNull
    public Presentation getSwitchConsoleActionPresentation() {
        return mySwitchConsoleAction.getTemplatePresentation();
    }

    /**
     * Allows filtering out unappropriate actions from toolbar.
     */
    @NotNull
    @Override
    public AnAction[] createConsoleActions() {
        AnAction[] primaryActions = myPrimaryConsoleView.createConsoleActions();
        AnAction[] secondaryActions = mySecondaryConsoleView.createConsoleActions();

        AnAction[] actions = new AnAction[4];
        actions[0] = mySwitchConsoleAction;

        actions[1] = new DelegatingToggleUseSoftWrapsToolbarAction(findAction(primaryActions, ToggleUseSoftWrapsToolbarAction.class));
        actions[2] = newDelegatingAction(primaryActions, secondaryActions, ScrollToTheEndToolbarAction.class);
        actions[3] = new ConsoleViewImpl.ClearAllAction(this);

        return actions;
    }

    private DelegatingScrollToTheEndToolbarAction newDelegatingAction(AnAction[] primaryActions, AnAction[] secondaryActions, Class<? extends AnAction> actionClass) {
        ScrollToTheEndToolbarAction primaryAction = findAction(primaryActions, ScrollToTheEndToolbarAction.class);
        ScrollToTheEndToolbarAction secondaryAction = findAction(secondaryActions, ScrollToTheEndToolbarAction.class);

        return new DelegatingScrollToTheEndToolbarAction(primaryAction, secondaryAction);
    }

    @SuppressWarnings("unchecked")
    private <T extends AnAction> T findAction(AnAction[] actions, Class<T> actionClass) {
        for (AnAction action : actions) {
            if (actionClass.isInstance(action)) {
                return (T) action;
            }
        }
        return null;
    }

    private class SwitchConsoleViewAction extends ToggleAction implements DumbAware {

        public SwitchConsoleViewAction() {
            super(message("switch.console.view.title"), null, SerialMonitorIcons.SwitchToHex);
        }

        @Override
        public boolean isSelected(final AnActionEvent event) {
            return !isPrimaryConsoleEnabled();
        }

        @Override
        public void setSelected(final AnActionEvent event, final boolean flag) {
            enableConsole(!flag);
            PropertiesComponent.getInstance().setValue(STATE_STORAGE_KEY, !flag);
        }
    }

    private class DelegatingScrollToTheEndToolbarAction extends ToggleAction implements DumbAware {

        private final ScrollToTheEndToolbarAction myPrimary;
        private final ScrollToTheEndToolbarAction mySecondary;

        public DelegatingScrollToTheEndToolbarAction(ScrollToTheEndToolbarAction primary, ScrollToTheEndToolbarAction secondary) {
            this.myPrimary = primary;
            this.mySecondary = secondary;

            // copy Presentation
            copyFrom(primary);
        }

        @Override
        public boolean isSelected(AnActionEvent e) {
            return isPrimaryConsoleEnabled() ? myPrimary.isSelected(e) : mySecondary.isSelected(e);
        }

        @Override
        public void setSelected(AnActionEvent e, boolean state) {
            myPrimary.setSelected(e, state);
            mySecondary.setSelected(e, state);
        }
    }

    private class DelegatingToggleUseSoftWrapsToolbarAction extends ToggleAction implements DumbAware {

        private final ToggleUseSoftWrapsToolbarAction myDelegate;

        public DelegatingToggleUseSoftWrapsToolbarAction(ToggleUseSoftWrapsToolbarAction delegate) {
            myDelegate = delegate;

            // copy Presentation
            copyFrom(delegate);
        }

        @Override
        public boolean isSelected(AnActionEvent e) {
            return myDelegate.isSelected(e);
        }

        @Override
        public void setSelected(AnActionEvent e, boolean state) {
            myDelegate.setSelected(e, state);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            // soft wraps make sense only for regular console view
            e.getPresentation().setEnabled(isPrimaryConsoleEnabled());
        }
    }
}
