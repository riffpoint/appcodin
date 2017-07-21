import static android.support.test.InstrumentationRegistry.getTargetContext;
import android.support.test.runner.AndroidJUnit4;

import com.app.lexicontrainer.Model.Card;
import com.app.lexicontrainer.Model.Data.DBHelper;
import com.app.lexicontrainer.Model.Data.DBWrapper;
import com.app.lexicontrainer.Model.Synonym;
import com.app.lexicontrainer.Model.Translate;
import com.app.lexicontrainer.Model.Unit;
import com.app.lexicontrainer.Model.Word;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DBWRapperTest {

    private DBWrapper m_DB;

    @Before
    public void setUp() throws Exception {
    getTargetContext().deleteDatabase(DBHelper.DB_NAME);
    m_DB = new DBWrapper(getTargetContext());
}

    @After
    public void tearDown() throws Exception {
        m_DB.close();
    }

    @Test
    public void shouldBeNotOpen() throws Exception {
        m_DB.open();
        m_DB.close();
        assertTrue(!m_DB.isOpen());
    }

    @Test
    public void shouldBeOpen() throws Exception {
        m_DB.open();
        assertTrue(m_DB.isOpen());
    }


    //DB query with Unit
    @Test
    public void shouldAddUnit(){
        m_DB.insertUnit(new Unit("Unit1"));
        List<Unit> units = m_DB.getAllUnits();
        assertThat(units.size(), is(1));
        assertTrue(units.get(0).getName().equals("Unit1"));
    }

    @Test
    public void shouldGetUnit(){
        m_DB.insertUnit(new Unit("Unit1"));
        long id = m_DB.insertUnit(new Unit("Unit2"));
        m_DB.insertUnit(new Unit("Unit3"));
        Unit unit = m_DB.getUnit(id);
        assertEquals(unit.getName(), "Unit2");
    }

    @Test
    public void shouldRemoveUnit(){
        Unit unit1 = new Unit("Unit1");
        Unit unit2 = new Unit("Unit2");
        Unit unit3 = new Unit("Unit3");

        unit1.setId(m_DB.insertUnit(unit1));
        unit2.setId(m_DB.insertUnit(unit2));
        unit3.setId(m_DB.insertUnit(unit3));
        List<Unit> units = m_DB.getAllUnits();
        assertThat(units.size(), is(3));
        m_DB.removeUnit(unit1);
        units = m_DB.getAllUnits();
        assertThat(units.size(), is(2));
        for(Unit u:units){
            assertNotEquals(u.getName(), "Unit2");
            assertNotEquals(u.getId(), unit1.getId());
        }
    }

    //DB query with Subunit
    @Test
    public void shouldAddSubunit(){
        m_DB.insertSubunit(new Subunit("Subunit1", 1));
        List<Subunit> subunits = m_DB.getAllSubunits();
        assertThat(subunits.size(), is(1));
        assertTrue(subunits.get(0).getName().equals("Subunit1"));
    }

    @Test
    public void shouldRemoveSubunit(){
        m_DB.insertSubunit(new Subunit("Subunit1", 1));
        long id = m_DB.insertSubunit(new Subunit("Subunit2", 1));
        m_DB.insertSubunit(new Subunit("Subunit3", 1));
        List<Subunit> subunits = m_DB.getAllSubunits();
        assertThat(subunits.size(), is(3));
        m_DB.removeSubunit(id);
        subunits = m_DB.getAllSubunits();
        assertThat(subunits.size(), is(2));
        for(Subunit u:subunits){
            assertNotEquals(u.getName(), "Subunit2");
            assertNotEquals(u.getId(), id);
        }
    }

    @Test
    public void shouldGetSubUnit(){
        m_DB.insertSubunit(new Subunit("Subunit1", 1));
        long id = m_DB.insertSubunit(new Subunit("Subunit2", 1));
        m_DB.insertSubunit(new Subunit("Subunit3", 1));
        Subunit subunit = m_DB.getSubunit(id);
        assertEquals(subunit.getName(), "Subunit2");
    }

    //DB query with Word
    @Test
    public void shouldAddWord(){
        m_DB.insertWord(new Word("Word1"));
        List<Word> words = m_DB.getWords();
        assertThat(words.size(), is(1));
        assertTrue(words.get(0).getExpression().equals("Word1"));
    }

    @Test
    public void shouldRemoveWord(){
        m_DB.insertWord(new Word("Word1"));
        long id = m_DB.insertWord(new Word("Word2"));
        m_DB.insertWord(new Word("Word3"));
        List<Word> words = m_DB.getWords();
        assertThat(words.size(), is(3));
        m_DB.removeWord(id);
        words = m_DB.getWords();
        assertThat(words.size(), is(2));
        for(Word w:words){
            assertNotEquals(w.getExpression(), "Word2");
            assertNotEquals(w.getId(), id);
        }
    }

    @Test
    public void shouldGetWord(){
        m_DB.insertWord(new Word("Word1"));
        long id = m_DB.insertWord(new Word("Word2"));
        m_DB.insertWord(new Word("Word3"));
        Word word = m_DB.getWord(id);
        assertEquals(word.getExpression(), "Word2");
    }

    //DB query with Translate
    @Test
    public void shouldAddTranslate(){
        m_DB.insertTranslate(new Translate("Translate1"));
        List<Translate> translates = m_DB.getTranslates();
        assertThat(translates.size(), is(1));
        assertTrue(translates.get(0).getExpression().equals("Translate1"));
    }

    @Test
    public void shouldRemoveTranslate(){
        m_DB.insertTranslate(new Translate("Translate1"));
        long id = m_DB.insertTranslate(new Translate("Translate2"));
        m_DB.insertTranslate(new Translate("Translate3"));
        List<Translate> translates = m_DB.getTranslates();
        assertThat(translates.size(), is(3));
        m_DB.removeTranslate(id);
        translates = m_DB.getTranslates();
        assertThat(translates.size(), is(2));
        for(Translate t:translates){
            assertNotEquals(t.getExpression(), "Translate2");
            assertNotEquals(t.getId(), id);
        }
    }

    @Test
    public void shouldGetTranslate(){
        m_DB.insertTranslate(new Translate("Translate1"));
        long id = m_DB.insertTranslate(new Translate("Translate2"));
        m_DB.insertTranslate(new Translate("Translate3"));
        Translate translate = m_DB.getTranslate(id);
        assertEquals(translate.getExpression(), "Translate2");
    }

    //DB query with Synonym
    @Test
    public void shouldAddSynonym(){
        m_DB.insertSynonym(new Synonym("Synonym1", 1));
        List<Synonym> synonyms = m_DB.getSynonyms();
        assertThat(synonyms.size(), is(1));
        assertTrue(synonyms.get(0).getExpression().equals("Synonym1"));
    }

    @Test
    public void shouldRemoveSynonym(){
        m_DB.insertSynonym(new Synonym("Synonym1", 1));
        long id = m_DB.insertSynonym(new Synonym("Synonym2", 1));
        m_DB.insertSynonym(new Synonym("Synonym3", 1));
        List<Synonym> synonyms = m_DB.getSynonyms();
        assertThat(synonyms.size(), is(3));
        m_DB.removeSynonym(id);
        synonyms = m_DB.getSynonyms();
        assertThat(synonyms.size(), is(2));
        for(Synonym s:synonyms){
            assertNotEquals(s.getExpression(), "Synonym2");
            assertNotEquals(s.getId(), id);
        }
    }

    @Test
    public void shouldReturnSynonymsOfWord(){
        long wordId2 = m_DB.insertWord(new Word("Word2"));
        long wordId1 = m_DB.insertWord(new Word("Word1"));
        long wordId3 = m_DB.insertWord(new Word("Word3"));
        m_DB.insertSynonym(new Synonym("Synonym1", wordId1));
        m_DB.insertSynonym(new Synonym("Synonym2", wordId1));
        m_DB.insertSynonym(new Synonym("Synonym3", wordId3));
        m_DB.insertSynonym(new Synonym("Synonym4", wordId1));
        m_DB.insertSynonym(new Synonym("Synonym5", wordId1));
        m_DB.insertSynonym(new Synonym("Synonym6", wordId3));
        List<Synonym> synonymsOfWord1 = m_DB.getSynonymsOfWord(wordId1);
        List<Synonym> synonymsOfWord2 = m_DB.getSynonymsOfWord(wordId2);
        List<Synonym> synonymsOfWord3 = m_DB.getSynonymsOfWord(wordId3);
        assertThat(synonymsOfWord1.size(), is(4));
        assertThat(synonymsOfWord2.size(), is(0));
        assertThat(synonymsOfWord3.size(), is(2));
    }

    @Test
    public void shouldGetSynonym(){
        m_DB.insertSynonym(new Synonym("Synonym1", 1));
        long id = m_DB.insertSynonym(new Synonym("Synonym2", 1));
        m_DB.insertSynonym(new Synonym("Synonym3", 1));
        Synonym synonym2 = m_DB.getSynonym(id);
        Synonym synonym = m_DB.getSynonym(synonym2.getId());
        assertEquals(synonym.getExpression(), "Synonym2");
    }

    //DB query with Card
    @Test
    public void shouldAddCard(){
        m_DB.insertCard(createCard());
        List<Card> cards = m_DB.getCards();
        assertThat(cards.size(), is(1));
        Word word = cards.get(0).getWord();
        Translate translate = cards.get(0).getTranslate();
        List<Synonym> synonyms1 = cards.get(0).getSynonyms();
        assertEquals(word.getExpression(), "Word1");
        assertEquals(translate.getExpression(), "Translate");
        assertThat(synonyms1.size(), is(2));
        assertThat(synonyms1.get(0).getExpression()==null, is(false));
    }

    private Card createCard(){
        Word word1 = new Word("Word1");
        long idWord1 = m_DB.insertWord(word1);
        Translate translate1 = new Translate("Translate");
        Synonym synonym1 = new Synonym("Synonym1", idWord1);
        Synonym synonym2 = new Synonym("Synonym2", idWord1);
        List<Synonym> synonyms = new ArrayList<>();
        synonyms.add(synonym1);
        synonyms.add(synonym2);
        Card card = new Card(word1, translate1, 1);
        card.setSynonyms(synonyms);
        long time= System.currentTimeMillis();
        card.setDateOfCreating(time);
        return card;
    }

    @Test
    public void shouldGetCard(){
        m_DB.insertCard(createCard());
        long id = m_DB.insertCard(createCard());
        m_DB.insertCard(createCard());
        Card card = m_DB.getCard(id);
        assertTrue(card!=null);
    }

    @Test
    public void shouldRemoveCard(){
        m_DB.insertCard(createCard());
        long id = m_DB.insertCard(createCard());
        m_DB.insertCard(createCard());
        List<Card> cards = m_DB.getCards();
        assertThat(cards.size(), is(3));
        long card2Time = cards.get(1).getDateOfCreating();
        long wordId = cards.get(1).getWord().getId();
        long translateId = cards.get(1).getTranslate().getId();
        List<Synonym> synonyms = cards.get(1).getSynonyms();

        assertTrue(synonyms!=null);
        assertThat(synonyms.size(), is(2));
        m_DB.removeCard(id);
        cards = m_DB.getCards();
        assertThat(cards.size(), is(2));
        Word word = m_DB.getWord(wordId);
        Translate translate = m_DB.getTranslate(translateId);
        assertTrue(word==null);
        assertTrue(translate==null);
        synonyms = cards.get(1).getSynonyms();
        for(Synonym s:synonyms){
            Synonym synonym = m_DB.getSynonym(s.getId());
            assertThat(synonym.getExpression()!=null, is(true));
        }
        for(Card c:cards){
            assertNotEquals(c.getDateOfCreating(), card2Time);
        }

    }




}
