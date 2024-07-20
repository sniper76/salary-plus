package ag.act.util;

import ag.act.converter.digitaldocument.DigitalDocumentItemResponseConverter;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.enums.DigitalAnswerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentItemTreeGeneratorTest {

    private DigitalDocumentItemTreeGenerator service;

    private List<DigitalDocumentItem> getItemList() {

        List<DigitalDocumentItem> list = new ArrayList<>();

        list.add(createChild(1L, null, 1, false));
        list.add(createChild(1L, 1L, 1, true));

        list.add(createChild(2L, null, 2, false));
        list.add(createChild(2L, 2L, 1, true));
        list.add(createChild(2L, 2L, 2, true));

        list.add(createChild(3L, null, 3, false));
        list.add(createChild(3L, 3L, 1, true));

        list.add(createChild(4L, null, 4, false));
        list.add(createChild(4L, 4L, 1, false));
        list.add(createChild(4L, 41L, 1, true));
        list.add(createChild(4L, 41L, 2, true));

        list.add(createChild(4L, 4L, 2, false));
        list.add(createChild(4L, 42L, 1, true));
        list.add(createChild(4L, 42L, 2, true));

        list.add(createChild(4L, 4L, 3, false));
        list.add(createChild(4L, 43L, 1, true));
        list.add(createChild(4L, 43L, 2, true));

        list.add(createChild(4L, 43L, 3, true));
        list.add(createChild(4L, 43L, 4, true));
        list.add(createChild(4L, 43L, 5, true));
        list.add(createChild(4L, 43L, 6, true));
        list.add(createChild(4L, 43L, 7, true));
        list.add(createChild(4L, 43L, 8, true));
        list.add(createChild(4L, 43L, 9, true));

        list.add(createChild(4L, 4L, 4, false));
        list.add(createChild(4L, 44L, 0, true));
        list.add(createChild(4L, 44L, 1, true));
        list.add(createChild(4L, 44L, 2, true));
        list.add(createChild(4L, 44L, 3, true));
        list.add(createChild(4L, 44L, 4, true));
        list.add(createChild(4L, 44L, 5, true));
        list.add(createChild(4L, 44L, 6, true));
        list.add(createChild(4L, 44L, 7, true));
        list.add(createChild(4L, 44L, 8, true));
        list.add(createChild(4L, 44L, 9, true));

        return list;
    }

    private DigitalDocumentItem createChild(long groupId, Long parentId, int myNumber, boolean isLastItem) {
        final Long id = Optional.ofNullable(parentId).orElse(0L) * 10 + myNumber;
        final String title = createTitle(id);

        DigitalDocumentItem item = new DigitalDocumentItem();

        item.setId(id);
        item.setParentId(parentId);
        item.setDefaultSelectValue(DigitalAnswerType.APPROVAL);
        item.setGroupId(groupId);
        item.setIsLastItem(isLastItem);
        item.setTitle(title + " 제목");
        item.setContent(title + " 내용");

        return item;
    }

    private String createTitle(Long id) {
        return String.format("제%s안", String.join("-", id.toString().split("")));
    }

    @Nested
    class GetTreeData {

        @BeforeEach
        void setUp() {
            //stubbing
            service = new DigitalDocumentItemTreeGenerator(new DigitalDocumentItemResponseConverter());
        }

        @Test
        void shouldReturnTreeData() {

            final List<DigitalDocumentItem> itemList = getItemList();
            final List<ag.act.model.DigitalDocumentItemResponse> actual = service.buildTree(itemList);

            assertThat(
                actual.size(),
                is(getItemList().stream().filter(e -> e.getParentId() == null).toList().size())
            );
            assertThat(actual.get(0).getTitle(), is(itemList.get(0).getTitle()));
            assertThat(actual.get(0).getChildItems().get(0).getTitle(), is(itemList.get(1).getTitle()));

            assertThat(actual.get(1).getTitle(), is(itemList.get(2).getTitle()));
            assertThat(actual.get(1).getChildItems().get(0).getTitle(), is(itemList.get(3).getTitle()));
            assertThat(actual.get(1).getChildItems().get(1).getTitle(), is(itemList.get(4).getTitle()));

            assertThat(actual.get(2).getTitle(), is(itemList.get(5).getTitle()));
            assertThat(actual.get(2).getChildItems().get(0).getTitle(), is(itemList.get(6).getTitle()));

            assertThat(actual.get(3).getTitle(), is(itemList.get(7).getTitle()));
            assertThat(actual.get(3).getChildItems().get(0).getTitle(), is(itemList.get(8).getTitle()));
            assertThat(actual.get(3).getChildItems().get(0).getChildItems().get(0).getTitle(), is(itemList.get(9).getTitle()));
            assertThat(actual.get(3).getChildItems().get(0).getChildItems().get(1).getTitle(), is(itemList.get(10).getTitle()));

            assertThat(actual.get(3).getChildItems().get(1).getTitle(), is(itemList.get(11).getTitle()));
            assertThat(actual.get(3).getChildItems().get(1).getChildItems().get(0).getTitle(), is(itemList.get(12).getTitle()));
            assertThat(actual.get(3).getChildItems().get(1).getChildItems().get(1).getTitle(), is(itemList.get(13).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getTitle(), is(itemList.get(14).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(0).getTitle(), is(itemList.get(15).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(1).getTitle(), is(itemList.get(16).getTitle()));

            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(2).getTitle(), is(itemList.get(17).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(3).getTitle(), is(itemList.get(18).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(4).getTitle(), is(itemList.get(19).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(5).getTitle(), is(itemList.get(20).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(6).getTitle(), is(itemList.get(21).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(7).getTitle(), is(itemList.get(22).getTitle()));
            assertThat(actual.get(3).getChildItems().get(2).getChildItems().get(8).getTitle(), is(itemList.get(23).getTitle()));
        }
    }
}