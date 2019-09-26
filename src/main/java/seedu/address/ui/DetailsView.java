package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.person.ScheduleStub;

public class DetailsView extends UiPart<Region> {

    @FXML
    private VBox detailsView;
    private static final String FXML = "DetailsView.fxml";

    public DetailsView(Person person) {
        super(FXML);
        this.detailsView = new VBox();
        PersonCard personCard = new PersonCard(person, 1);
        ScheduleStub stub = new ScheduleStub(1);
        ScheduleView scheduleView = new ScheduleView(stub);
        detailsView.getChildren().addAll(personCard.getRoot(), scheduleView.getScheduleView());
    }

    public ScrollPane getDetailsView() {
        ScrollPane sp = new ScrollPane();
        sp.setContent(this.detailsView);
        return sp;
    }
}
