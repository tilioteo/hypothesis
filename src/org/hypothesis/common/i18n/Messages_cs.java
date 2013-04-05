/**
 * 
 */
package org.hypothesis.common.i18n;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Locale strings for Czech
 */
public class Messages_cs extends Messages {

	static final Object[][] contents_cs = {
			{ TEXT_MANAGER_APP_TITLE, "Hypothesis manager" },
			{ TEXT_APP_TITLE, "Hypothesis" },
			{ TEXT_APP_DESCRIPTION, "Testování hypotéz" },
			{ TEXT_BUTTON_CHANGE_USER_DATA, "Změnit osobní údaje" },
			{ TEXT_BUTTON_MANAGE_GROUPS, "Správa skupin" },
			{ TEXT_BUTTON_MANAGE_USERS, "Správa uživatelů" },
			{ TEXT_BUTTON_MANAGE_PERMITIONS, "Správa oprávnění" },
			{ TEXT_BUTTON_LOGOUT, "Odhlásit" },
			{ PATTERN_DATE_FORMAT, "d.M.yyyy" },
			{ TEXT_LOGGED_USER, "přihlášený uživatel:" },
			{ TEXT_BUTTON_SAVE, "Uložit" },
			{ TEXT_BUTTON_CANCEL, "Storno" },
			{ TEXT_BUTTON_ADD, "Přidat" },
			{ TEXT_BUTTON_UPDATE, "Upravit" },
			{ TEXT_BUTTON_DELETE, "Smazat" },
			{ TEXT_BUTTON_START, "Spustit test" },
			{ TEXT_SELECTED, "Vybrané" },
			{ TEXT_ALL, "Všechny" },
			{ TEXT_BUTTON_EXPORT, "Exportovat" },
			{ ERROR_SAVE_FAILED, "Uložení se nezdařilo" },
			{ ERROR_DELETE_FAILED, "Smazání se nezdařilo." },
			{ TEXT_YES, "Ano" },
			{ TEXT_NO, "Ne" },
			{ TEXT_ID, "ID" },
			{ TEXT_NAME, "Jméno" },
			{ TEXT_USER, "Uživatel" },
			{ TEXT_ROLES, "Role" },
			{ TEXT_GROUPS, "Skupiny" },
			{ TEXT_ACTIVE, "Aktivní" },
			{ TEXT_EXPIRE_DATE, "Datum expirace" },
			{ TEXT_NOTE, "Poznámka" },
			{ TEXT_TEST, "Test" },
			{ TEXT_ENABLED_PACKS, "Přístupné testy" },
			{ TEXT_ENABLED_GROUPS, "Povolené skupiny" },
			{ TEXT_DISABLED_USERS, "Zakázaní uživatelé" },
			{ TEXT_NAME_REQUIRED, "Prosím zadejte jméno." },
			{ TEXT_GENERATED_GROUP_REQUIRED,
					"Prosím zadejte skupinu pro generátor jmen." },
			{ TEXT_GENERATED_COUNT_REQUIRED,
					"Prosím zadejte počet uživatelů pro generátor jmen." },
			{ TEXT_PASSWORD_REQUIRED, "Prosím zadejte heslo." },
			{ TEXT_NAME_LENGTH_REQUIRED_FMT,
					"Jméno musí být délky %d-%d znaků." },
			{ TEXT_GENERATED_GROUP_LENGTH_REQUIRED_FMT,
					"Generovaný název skupiny musí být délky %d-%d znaků." },
			{ TEXT_GENERATED_COUNT_INTEGER,
					"Generovaný počet uživatelů musí být číslo od 1 do 999." },
			{ TEXT_PASSWORD_LENGTH_REQUIRED_FMT,
					"Heslo musí být délky %d-%d znaků." },
			{ TEXT_NOT_OWN_ANY_USERS, "Nevlastníte žádné uživatele." },
			{ TEXT_NOT_OWN_ANY_GROUPS, "Nevlastníte žádné skupiny." },
			{ TEXT_GROUP_NAME_EXISTS, "Jméno skupiny již existuje." },
			{ TEXT_USER_NAME_EXISTS, "Jméno uživatele již existuje." },
			{ TEXT_ROLE_REQUIRED, "Prosím vyberte roli." },
			{ TEXT_GROUP_REQUIRED,
					"Prosím zařaďte uživatele do některé ze svých skupin." },
			{ TEXT_ERROR, "Chyba" },
			{ ERROR_APP_INITIALIZATION_FMT,
					"Inicializace aplikace se nezdařila. %s" },
			{ TEXT_CHECK_FOR_UPDATE,
					"Vyberte vlastnosti, které chcete hromadně upravit." },
			{ TEXT_TEST_FINISHED,
					"Test byl dokončen, stisknutím tlačítka zavřete okno" },
			{ TEXT_BUTTON_CLOSE_TEST, "Zavřít" },

			{ ERROR_BAD_LOGIN, "Nesprávné jméno nebo heslo!" },
			{ ERROR_NOT_ENABLED_USER, "Váš účet je zablokovaný." },
			{ ERROR_UNSUFFICIENT_RIGHTS_ADMIN,
					"Nemáte oprávnění pro přístup do administrace." },

			{ TEXT_LABEL_USERNAME, "Uživatel" },
			{ TEXT_LABEL_PASSWORD, "Heslo" },
			{ TEXT_BUTTON_LOGIN, "Přihlásit" },
			{ TEXT_MANAGER_LOGIN_TITLE, "Hypothesis manager - Přihášení" },
			{ TEXT_LOGIN_TITLE, "Hypothesis - Přihášení" },
			{ TEXT_MANAGER_LOGIN_HEADER, "Hypothesis - správa uživatelů" },
			{ TEXT_LOGIN_HEADER, "Hypothesis - testování" },
			{ ERROR_LOGIN_FAILED, "Přihlášení se nezdařilo." },
			{ ERROR_LOAD_RESOURCE, "Při načítání stránky došlo k chybě." },

			{ TEXT_NEW_GROUP_TITLE, "Nová skupina" },
			{ TEXT_UPDATE_GROUP_TITLE, "Úprava skupiny" },
			{ TEXT_UPDATE_GROUPS_TITLE, "Úprava skupin" },
			{ INFO_GROUP_CREATED, "Skupina byla přidána." },

			{ TEXT_NEW_USER_TITLE, "Nový uživatel" },
			{ TEXT_UPDATE_USER_TITLE, "Úprava uživatele" },
			{ TEXT_UPDATE_USERS_TITLE, "Úprava uživatelů" },
			{ TEXT_DISABLED_PACKS, "Zakázané testy" },
			{ INFO_USER_CREATED, "Uživatel byl přidán." },
			{ INFO_USERS_CREATED, "Uživatelé byli přidáni." },

			{ TEXT_EDIT_GROUPS_TITLE, "Správa skupin" },
			{ TEXT_ADD_GROUP_TITLE, "Přidat skupinu" },
			{ INFO_GROUP_SAVED, "Skupina byla uložena." },
			{ INFO_GROUP_DELETED, "Skupina byla smazána." },
			{ INFO_GROUPS_DELETED, "Skupiny byly smazány." },
			{ TEXT_TOTAL_USERS_FMT, "celkem uživatelů: %d" },
			{ TEXT_TOTAL_PACKS_FMT, "celkem testů: %d" },
			{ TEXT_NO_GROUPS_SELECTED, "Nejsou vybrány žádné skupiny." },

			{ TEXT_EDIT_PERMITIONS_TITLE, "Změna přístupových práv" },

			{ TEXT_EDIT_PERSONAL_DATA_TITLE, "Změna osobních údajů" },
			{
					TEXT_EDIT_PERSONAL_DATA_INFO,
					"Pro editaci osobních údajů vyplňte nové hodnoty a stiskněte tlačítko \"Uložit\"." },
			{ INFO_PERSONAL_DATA_SAVED, "Údaje byly uloženy." },

			{ TEXT_EDIT_USERS_TITLE, "Správa uživatelů" },
			{ TEXT_EDIT_SELECTED_USER_TITLE, "Upravit vybraného uživatele" },
			{ TEXT_EDIT_SELECTED_USERS_TITLE, "Upravit vybrané uživatele" },
			{ TEXT_ADD_USER_TITLE, "Přidat uživatele" },
			{ WARN_CREATE_GROUP_FIRST,
					"Pro přidání uživatele si musíte nejprve vytvořit skupinu" },
			{ INFO_USER_SAVED, "Uživatel byl uložen." },
			{ INFO_USER_DELETED, "Uživatel byl smazán." },
			{ INFO_USERS_DELETED, "Uživatelé byli smazáni." },
			{ WARN_USER_ROLE_CHANGED, "Upozornění: Vaše role byla změněna." },
			{ WARN_AT_LEAST_ONE_SU,
					"V systému musí zůstat alespoň jeden SUPERUSER." },
			{ WARN_ONLY_SU_CAN_DELETE_SU,
					"Pouze uživatel v roli SUPERUSER může smazat jiného SUPERUSERa." },
			{ TEXT_TOTAL_GROUPS_FMT, "celkem skupin: %d" },
			{ TEXT_NO_USERS_SELECTED, "Nejsou vybráni žádní uživatelé." },
			{ TEXT_GENERATE, "vygenerovat podle vzoru skupina-001-XXXX" },

			{
					TEXT_BASE_INFO,
					"Vítejte v aplikaci Hypothesis. Začněte výběrem kteréhokoliv z přístupných testů níže..." },
			{ TEXT_TEST_INFO, "Informace o testu..." },

			{ TEXT_EXPORT_USERS_SHEET_NAME, "Hypothesis uživatelé" },
			{ TEXT_EXPORT_USERS_FILE_NAME, "hypothesis-uzivatele.xls" },
			{ TEXT_EXPORT_GROUPS_SHEET_NAME, "Hypothesis skupiny" },
			{ TEXT_EXPORT_GROUPS_FILE_NAME, "hypothesis-skupiny.xls" },
			{ ERROR_EXPORT_CANNOT_CREATE_FILE,
					"Nepodařilo se vytvořit exportní soubor." },
			{ ERROR_EXPORT_ROWS_LIMIT_EXCEEDED,
					"Překročen limit počtu řádků v excelové tabulce." },
			{ ERROR_EXPORT_CANNOT_WRITE_TO_FILE,
					"Nemohu zapisovat do exportního souboru." },

			{ TEXT_DELETE_CONFIRM, "Potvrďte prosím smazání" },
			{ TEXT_DELETE_SELECTED_USERS,
					"Opravdu chcete smazat vybrané uživatele?" },
			{ TEXT_DELETE_ALL_USERS, "Opravdu chcete smazat všechny uživatele?" },
			{ TEXT_DELETE_SELECTED_GROUPS,
					"Opravdu chcete smazat vybrané skupiny?" },
			{ TEXT_DELETE_ALL_GROUPS, "Opravdu chcete smazat všechny skupiny?" }, };

	@Override
	public Object[][] getContents() {
		return contents_cs;
	}
}
