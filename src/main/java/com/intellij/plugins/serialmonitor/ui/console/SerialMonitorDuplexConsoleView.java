package com.intellij.plugins.serialmonitor.ui.console;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.console.DuplexConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;

import org.jetbrains.annotations.NotNull;

import icons.SerialMonitorIcons;

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
        actions[3] = new ClearAllAction(this);

        return actions;
    }

    private DelegatingScrollToTheEndToolbarAction newDelegatingAction(AnAction[] primaryActions, AnAction[] secondaryActions,
                                                                      Class<? extends AnAction> actionClass) {
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
            PropertiesComponent.getInstance().setValue(STATE_STORAGE_KEY, Boolean.toString(!flag));
        }
    }

    private class DelegatingScrollToTheEndToolbarAction extends AnAction implements DumbAware {

        private final ScrollToTheEndToolbarAction myPrimary;
        private final ScrollToTheEndToolbarAction mySecondary;

        public DelegatingScrollToTheEndToolbarAction(ScrollToTheEndToolbarAction primary, ScrollToTheEndToolbarAction secondary) {
            this.myPrimary = primary;
            this.mySecondary = secondary;

            // copy Presentation
            copyFrom(primary);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            myPrimary.actionPerformed(e);
            mySecondary.actionPerformed(e);
        }

        @Override
        public void update(AnActionEvent e) {
            AnAction currentViewAction = isPrimaryConsoleEnabled() ? myPrimary : mySecondary;

            if(currentViewAction instanceof ToggleAction) {
                // Idea Community and others use ToggleAction for ScrollToTheEndToolbarAction
                // since I cannot access internal Editor field, I use action itself to calculate state
                boolean selected = ((ToggleAction) currentViewAction).isSelected(e);
                e.getPresentation().setEnabled(!selected);
            } else {
                // Android Studio is known to replace ToggleAction with plain AnAction, disabling button
                currentViewAction.update(e);
            }
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

    private static class ClearAllAction extends DumbAwareAction {

        private final ConsoleView myConsoleView;

        public ClearAllAction(ConsoleView consoleView) {
            super(ExecutionBundle.message("clear.all.from.console.action.name"), "Clear the contents of the console", AllIcons.Actions.GC);
            myConsoleView = consoleView;
        }

        @Override
        public void update(AnActionEvent e) {
            boolean enabled = myConsoleView != null && myConsoleView.getContentSize() > 0;
            if (!enabled) {
                enabled = e.getData(LangDataKeys.CONSOLE_VIEW) != null;
                Editor editor = e.getData(CommonDataKeys.EDITOR);
                if (editor != null && editor.getDocument().getTextLength() == 0) {
                    enabled = false;
                }
            }
            e.getPresentation().setEnabled(enabled);
        }

        @Override
        public void actionPerformed(final AnActionEvent e) {
            final ConsoleView consoleView = myConsoleView != null ? myConsoleView : e.getData(LangDataKeys.CONSOLE_VIEW);
            if (consoleView != null) {
                consoleView.clear();
            }
        }
    }
}
