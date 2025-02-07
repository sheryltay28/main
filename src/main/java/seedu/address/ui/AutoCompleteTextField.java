package seedu.address.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seedu.address.logic.commands.CommandMasterList;

//@@author SebastianLie
/**
 * AutoComplete added to textfield.
 * listens to user input and populates
 * drop down menu with existing commands
 * solution adopted from https://stackoverflow.com/questions/36861056/javafx-textfield-auto-suggestions
 */
public class AutoCompleteTextField extends TextField {

    private final SortedSet<String> entries;
    private ContextMenu entriesPopup;

    /**
     * initialises all commands and their usages,
     * popup-menu as well as listener
     */
    public AutoCompleteTextField() {
        super();
        this.entries = new TreeSet<>();
        this.entriesPopup = new ContextMenu();
        entries.addAll(CommandMasterList.getCommandWords());
        setListener();
    }

    /**
     * "Suggestion" specific listener
     */
    private void setListener() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();
            if (enteredText == null || enteredText.isEmpty()) {
                entriesPopup.hide();
            } else {
                //filter all possible suggestions depends on "Text", case insensitive
                List<String> filteredEntries = entries.stream()
                        .filter(e -> e.toLowerCase().contains(enteredText.toLowerCase()))
                        .collect(Collectors.toList());
                //some suggestions are found
                if (!filteredEntries.isEmpty()) {
                    //build popup - list of "CustomMenuItem"
                    populatePopup(filteredEntries, enteredText);
                    if (!entriesPopup.isShowing()) { //optional
                        entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0); //position of popup
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });
        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            entriesPopup.hide();
        });
    }

    /**
     * populates contextmenu with suggestions
     * of command to be entered
     * if any suggestion is selected,
     * set the textfield to suggestion
     * @param searchResult
     * @param searchRequest
     */
    private void populatePopup(List<String> searchResult, String searchRequest) {
        List<CustomMenuItem> menuItems = new LinkedList<>();

        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);

        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);

            Label entryLabel = new Label();
            entryLabel.setGraphic(Styles.buildTextFlow(result, searchRequest));
            entryLabel.setPrefHeight(10);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            menuItems.add(item);

            item.setOnAction(actionEvent -> {
                setText(result);
                positionCaret(result.length());
                entriesPopup.hide();
            });
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
    public void addEntry(String toAdd) {
        entries.add(toAdd);
    }
}
