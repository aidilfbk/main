package seedu.address.model;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.javafaker.Faker;

import seedu.address.model.group.GroupDescriptor;
import seedu.address.model.group.GroupList;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupRemark;
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
    private static final int targetNumPeople = 1000;
    private static final int maxPerGroup = 6;

    private static final Random random = new Random(2103);
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

        final Set<String> usedPersonNames = new HashSet<>();

        for (int i = 0; i < targetNumPeople; i++) {
            final PersonDescriptor personDescriptor = new PersonDescriptor();

            String fullName = null;
            String username = null;
            int nameGenerationTries = 0;
            do {
                final String firstName = faker.name().firstName();
                final String lastName = faker.name().lastName();
                fullName = StringUtils.joinWith(" ", firstName, lastName);

                username = StringUtils.deleteWhitespace(StringUtils.join(
                        firstName.replaceAll("'", "").toLowerCase(),
                        ".",
                        lastName.replaceAll("'", "").toLowerCase()
                ));
                if (nameGenerationTries++ > 50) {
                    throw new RuntimeException(
                            "Tried 50 times in a row to generate a unique person name, "
                                    + "seems like all of them have been used"
                    );
                }
            } while (!usedPersonNames.add(fullName));

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

        final int numPeople = personList.getPersons().size();

        final int targetNumGroups = faker.number().numberBetween(numPeople / maxPerGroup, numPeople / 2);

        final Set<String> usedGroupNames = new HashSet<>();

        for (int i = 0; i < targetNumGroups; i++) {
            final GroupDescriptor groupDescriptor = new GroupDescriptor();
            String name;
            int nameGenerationTries = 0;
            do {
                switch (faker.number().numberBetween(0, 14)) {
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
                case 11:
                    name = faker.app().name();
                    break;
                case 12:
                    name = faker.esports().team();
                    break;
                case 13:
                    name = faker.team().name();
                    break;
                default:
                    name = null;
                }
                assert name != null;

                if (nameGenerationTries++ > 50) {
                    throw new RuntimeException(
                            "Tried 50 times in a row to generate a unique group name, "
                                    + "seems like all of them have been used"
                    );
                }
            } while (!usedGroupNames.add(name));

            groupDescriptor.setGroupName(new GroupName(name));
            if (faker.bool().bool()) {
                groupDescriptor.setGroupRemark(new GroupRemark(faker.company().catchPhrase()));
            }
            groupList.addGroup(groupDescriptor);
        }

        final int numGroups = groupList.getGroups().size();

        for (var group : groupList.getGroups()) {
            final int memberCount = faker.number().numberBetween(2, maxPerGroup + 1);
            for (int i = 0; i < memberCount; i++) {
                final int personId = faker.number().numberBetween(0, numPeople + 1);
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
                "%d people, %d groups and %d person-group mappings generated.\n\n"
                        + "Data saved to %s.\n"
                        + "Hopefully Windows Explorer/Finder is showing it right now...",
                personList.getPersons().size(),
                numGroups,
                personGroupMapping.asUnmodifiableObservableList().size(),
                savePath.toAbsolutePath().toString()
        ));
    }
}
