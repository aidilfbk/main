package seedu.address.model;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.github.javafaker.Faker;

import seedu.address.model.group.GroupDescriptor;
import seedu.address.model.group.GroupList;
import seedu.address.model.group.GroupName;
import seedu.address.model.mapping.PersonToGroupMapping;
import seedu.address.model.mapping.PersonToGroupMappingList;
import seedu.address.model.mapping.Role;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonDescriptor;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.PersonList;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.storage.JsonTimeBookStorage;

/**
 * Class to generate test data.
 */
class TestDataGeneratorUtil {
    private static final Path savePath = Path.of("data/timebook-generated.json");
    private static final int numberOfPeople = 1000;
    private static final int maxPerGroup = 6;

    private static final Random random = new Random(0);
    private static final Faker faker = new Faker(new Locale("en-SG"), random);

    /**
     * Generates test data and saves it to the {@link #savePath}.
     *
     * @param unused Unused arguments.
     * @throws IOException If saving of the test data fails.
     */
    public static void main(String[] unused) throws IOException {
        final PersonList personList = new PersonList();
        final GroupList groupList = new GroupList();
        final PersonToGroupMappingList personGroupMapping = new PersonToGroupMappingList();

        final int numGroups = faker.number().numberBetween(numberOfPeople / maxPerGroup, numberOfPeople / 2);

        for (int i = 0; i < numGroups; i++) {
            final GroupDescriptor groupDescriptor = new GroupDescriptor();
            String name;
            switch (faker.number().numberBetween(0, 11)) {
            case 0:
                name = faker.space().constellation();
                break;
            case 1:
                name = faker.space().galaxy();
                break;
            case 2:
                name = faker.space().meteorite();
                break;
            case 3:
                name = faker.space().moon();
                break;
            case 4:
                name = faker.space().nebula();
                break;
            case 5:
                name = faker.space().star();
                break;
            case 6:
                name = faker.space().starCluster();
                break;
            case 7:
                name = faker.ancient().god();
                break;
            case 8:
                name = faker.ancient().hero();
                break;
            case 9:
                name = faker.ancient().primordial();
                break;
            case 10:
                name = faker.ancient().titan();
                break;
            default:
                name = null;
            }
            assert name != null;

            groupDescriptor.setGroupName(new GroupName(name));

            groupList.addGroup(groupDescriptor);
        }

        for (int i = 0; i < 1000; i++) {
            final PersonDescriptor personDescriptor = new PersonDescriptor();

            final String firstName = faker.name().firstName();
            final String lastName = faker.name().lastName();
            final String fullName = StringUtils.joinWith(" ", firstName, lastName);

            final String username = StringUtils.deleteWhitespace(StringUtils.join(
                    firstName.replaceAll("'", "").toLowerCase(),
                    ".",
                    lastName.replaceAll("'", "").toLowerCase()
            ));

            personDescriptor.setName(new Name(fullName.replaceAll("[-]", " ")));
            if (faker.bool().bool()) {
                personDescriptor.setPhone(new Phone(
                        faker.phoneNumber().phoneNumber().substring(4).replaceAll(" ", "")
                ));
            }
            if (faker.bool().bool()) {
                personDescriptor.setEmail(new Email(faker.internet().safeEmailAddress(username)));
            }
            if (faker.bool().bool()) {
                personDescriptor.setAddress(new Address(faker.address().streetAddress()));
            }

            String remark;
            switch (faker.number().numberBetween(0, 18)) {
            case 0:
                remark = String.format("Plays %s", faker.esports().game());
                break;
            case 1:
                remark = String.format("Enjoys listening to %s", faker.music().genre());
                break;
            case 2:
                remark = String.format("Plays the %s", faker.music().instrument());
                break;
            case 3:
                remark = String.format("Interned at %s", faker.company().name());
                break;
            case 4:
                remark = String.format("Likes the colour %s", faker.color().name());
                break;
            case 5:
                remark = String.format("Enjoys reading %s novels", faker.book().author());
                break;
            case 6:
                remark = String.format("Has a dog called %s", faker.dog().name());
                break;
            case 7:
                remark = String.format("Has a cat called %s", faker.cat().name());
                break;
            default:
                remark = null;
            }
            if (remark != null) {
                personDescriptor.setRemark(new Remark(remark));
            }

            personList.addPerson(personDescriptor);
        }

        for (var group : groupList.getGroups()) {
            final int memberCount = faker.number().numberBetween(2, maxPerGroup + 1);
            for (int i = 0; i < memberCount; i++) {
                final int personId = faker.number().numberBetween(0, numberOfPeople + 1);
                Role role = null;
                PersonToGroupMapping mapping;

                if (faker.bool().bool()) {
                    role = new Role(faker.job().position());
                }

                if (role != null) {
                    mapping = new PersonToGroupMapping(
                            new PersonId(personId),
                            group.getGroupId(),
                            role
                    );
                } else {
                    mapping = new PersonToGroupMapping(
                            new PersonId(personId),
                            group.getGroupId()
                    );
                }

                personGroupMapping.addPersonToGroupMapping(mapping);
            }
        }

        final TimeBook timeBook = new TimeBook(personList, groupList, personGroupMapping);
        final JsonTimeBookStorage jsonTimeBookStorage = new JsonTimeBookStorage(savePath);
        jsonTimeBookStorage.saveTimeBook(timeBook);

        Desktop.getDesktop().browseFileDirectory(savePath.toFile());

        System.out.println(String.format(
                "Generated data saved to %s.\nHopefully Windows Explorer/Finder is showing it right now...",
                savePath.toAbsolutePath().toString()
        ));
    }
}
