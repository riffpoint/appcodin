//
//  RPCNContactManager.m
//
//  Created by AppCodin <info@appcodin.com> on 7/6/16.
//  Copyright © 2016 AppCodin <info@appcodin.com>. All rights reserved.
//

#import "RPCNContactManager.h"

#import "ContactsFromServerEntity.h"
#import "ContactSyncEntity.h"
#import "ContactsEntity.h"
#import "UserEntity.h"

#import "NSString+Utils.h"

#pragma mark - Staic

static NSString * const kWorkPhonKey = @"workPhone";
static NSString * const kHomePhonKey = @"homePhone";
static NSString * const kMobilePhonKey = @"mobilePhone";


#pragma mark - Private interface 

@interface RPCNContactManager()

#pragma mark - Properties

@property (nonatomic, strong) CNContactStore *addressBook;
@property (nonatomic, strong) NSArray *cnContacts;

@end

#pragma mark - Public 

@implementation RPCNContactManager

#pragma mark - Lifecycle

+ (instancetype)sharedInstance {
    static RPCNContactManager* instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[RPCNContactManager alloc] init];
    });
    
    return instance;
}

- (id)init {
    self = [super init];
    if (self) {
        self.addressBook = [[CNContactStore alloc] init];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(contactStoreWasUpdated) name:CNContactStoreDidChangeNotification object:nil];
    }
    
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Handle access to the phone's contacts

- (void)createAddressBookWithCompletionBlock:(void(^)(CNContactStore *theAddressBook, NSArray* thePeopleFromAddressBook))aCompletionBlock {
    switch ([CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts]) {
            // Update our UI if the user has granted access to their Contacts
        case CNAuthorizationStatusAuthorized: {
            [self fetchPeopleWithAddressBook:self.addressBook completionBlock:aCompletionBlock];
        }
            break;
            // Prompt the user for access to Contacts if there is no definitive answer
        case CNAuthorizationStatusNotDetermined: {
            [self.addressBook requestAccessForEntityType:CNEntityTypeContacts completionHandler:^(BOOL granted, NSError * _Nullable error) {
                if (granted) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self fetchPeopleWithAddressBook:self.addressBook completionBlock:aCompletionBlock];
                    });
                }
                else {
                    aCompletionBlock(NULL, nil);
                }
            }];
        }
            break;
            // Display a message if the user has denied or restricted access to Contacts
        case CNAuthorizationStatusDenied:
        case CNAuthorizationStatusRestricted: {
            aCompletionBlock(NULL, nil);
        }
            
            break;
        default:
            aCompletionBlock(NULL, nil);
            break;
    }
}

#pragma mark - Fetch methods

- (void)fetchPeopleWithAddressBook:(CNContactStore *)store completionBlock:(void(^)(CNContactStore *theAddressBook, NSArray *thePeopleFromAddressBook))aCompletionBlock {
    NSArray *keysToFetch = @[CNContactFamilyNameKey, CNContactGivenNameKey, CNContactMiddleNameKey, CNContactPhoneNumbersKey, CNContactImageDataKey, CNContactEmailAddressesKey, CNContactPostalAddressesKey, CNContactNameSuffixKey, CNContactOrganizationNameKey, CNContactJobTitleKey, CNContactBirthdayKey, CNContactNoteKey, CNContactUrlAddressesKey, CNContactNicknameKey, CNContactNamePrefixKey];
    NSPredicate *predicate = [CNContact predicateForContactsInContainerWithIdentifier:store.defaultContainerIdentifier];
    NSError *error;
    NSArray *cnContacts = [store unifiedContactsMatchingPredicate:predicate keysToFetch:keysToFetch error:&error];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
            aCompletionBlock(store, cnContacts);
    });
}

- (void)nameFromContactBookWithEmail:(NSString*)anEmail people:(CNContactStore *)aPeople withNameBlock:(void(^)(NSString *name))block {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
        [self getUserFromAddressBookWithEmail:anEmail people:aPeople withContactBlock:^(CNContact *cnContact) {
            if (cnContact) {
                NSString *compositeName;
                if ([cnContact.familyName removeWhitespace].length == 0 && [cnContact.givenName removeWhitespace].length == 0) {
                    compositeName = cnContact.emailAddresses.firstObject.value;
                }
                else {
                    compositeName = [NSString stringWithFormat:@"%@ %@",cnContact.givenName, cnContact.familyName];
                }
                
                block(compositeName);
            }
            else {
                block(nil);
            }
        }];
    });
}

- (void)getUserFromAddressBookWithEmail:(NSString*)anEmail people:(CNContactStore *)anStore withContactBlock:(void(^)(CNContact *contact))block {
    NSArray *keys = @[CNContactFamilyNameKey, CNContactGivenNameKey, CNContactPhoneNumbersKey, CNContactImageDataKey,CNContactEmailAddressesKey, CNContactImageDataAvailableKey, CNContactThumbnailImageDataKey];
    NSPredicate *predicate = [CNContact predicateForContactsInContainerWithIdentifier:anStore.defaultContainerIdentifier];
    NSError *error;
    BOOL wasEmailFound = NO;
    
    if (!self.cnContacts) {
        self.cnContacts = [anStore unifiedContactsMatchingPredicate:predicate keysToFetch:keys error:&error];
    }
    
    NSPredicate *beginsPredicate = [NSPredicate predicateWithFormat:@"(SELF beginswith[cd] %@)", anEmail];
    if (self.cnContacts.count == 0) {
        block(nil);
    }
    else {
        for (CNContact *contact in self.cnContacts) {
            for (CNLabeledValue<NSString*>* labeledValue in contact.emailAddresses) {
                if ([beginsPredicate evaluateWithObject: labeledValue.value]) {
                    wasEmailFound = YES;
                    block(contact);
                    break;
                }
            }
            if(wasEmailFound ) {
                wasEmailFound = NO;
                break;
            }
        }
        block(nil);
    }
}

- (NSString *)nameFromContactBookWithEmail:(NSString *)anEmail people:(CNContactStore *)aPeople {
    CNContact *cnContact =  [self getUserFromAddressBookWithEmail: anEmail people: aPeople];
    if (cnContact) {
        return [NSString stringWithFormat:@"%@ %@",cnContact.givenName, cnContact.familyName];
    }
    else {
        return nil;
    }
    
}

- (CNContact *)getUserFromAddressBookWithEmail:(NSString *)anEmail people:(CNContactStore *)anStore {
    NSArray *keys = @[CNContactFamilyNameKey, CNContactGivenNameKey, CNContactPhoneNumbersKey, CNContactImageDataKey, CNContactEmailAddressesKey, CNContactImageDataAvailableKey, CNContactThumbnailImageDataKey];
    NSPredicate *predicate = [CNContact predicateForContactsInContainerWithIdentifier:anStore.defaultContainerIdentifier];
    NSError *error;
    if (!self.cnContacts) {
        self.cnContacts = [anStore unifiedContactsMatchingPredicate:predicate keysToFetch:keys error:&error];
    }
    NSPredicate *beginsPredicate = [NSPredicate predicateWithFormat:@"(SELF beginswith[cd] %@)", anEmail];
    for (CNContact *contact in self.cnContacts) {
        for (CNLabeledValue<NSString *> * labeledValue in contact.emailAddresses) {
            if ([beginsPredicate evaluateWithObject: labeledValue.value]) {
                return contact;
                break;
            }
        }
    }
    
    return nil;
}

- (NSString *)getContactNamesWithEmail:(NSString *)email source:(CNContactStore *)store {
    NSString *fullName = @"";
    CNContact *contact = [self getUserFromAddressBookWithEmail:email people:(store == nil ? self.addressBook : store)];
    if(contact != nil) {
        NSString *givenName = contact.givenName;
        NSString *familyName = contact.familyName;
        
        if(givenName.length > 0 || familyName.length > 0) {
            if(givenName.length > 0) {
                fullName = givenName;
            }
            if(familyName.length > 0) {
                if(fullName.length > 0) {
                    fullName = [fullName stringByAppendingFormat:@" %@", familyName];
                }
                else {
                    fullName = familyName;
                }
            }
        }
    }
    
    return fullName;
}

- (NSMutableDictionary *)getPhoneFromContactBookWithEmail:(NSString *)anEmail people:(CNContactStore *)store {
    CNContact *cnContact = [self getUserFromAddressBookWithEmail:anEmail people:store];
    NSMutableDictionary *arrayPhones = [NSMutableDictionary dictionary];
    if (cnContact) {
        if (cnContact.phoneNumbers.count > 0) {
            for (CNLabeledValue<CNPhoneNumber*>* labeledValue in cnContact.phoneNumbers) {
                CNPhoneNumber *number = labeledValue.value;
                [arrayPhones setObject:[NSString stringWithFormat:@"%@ %@", cnContact.givenName, cnContact.familyName] forKey:number.stringValue];
                
            }
        }
    }
    
    ContactsFromServerEntity *contact = [ContactsFromServerEntity loadContactByEmail:anEmail].firstObject;
    
    if (contact != nil) {
        if ((contact.workPhone != nil) && [contact.workPhone removeWhitespace].length > 0 && ![contact.workPhone isEqualToString:@"(null)"]) {
            [arrayPhones setObject:contact.workPhone forKey:kWorkPhonKey];
        }
        
        if ((contact.homePhone != nil) && [contact.homePhone removeWhitespace].length > 0 && ![contact.homePhone isEqualToString:@"(null)"]) {
            [arrayPhones setObject:contact.homePhone forKey:kHomePhonKey];
        }
        
        if ((contact.mobilePhone != nil) && [contact.mobilePhone removeWhitespace].length > 0 && ![contact.mobilePhone isEqualToString:@"(null)"]) {
            [arrayPhones setObject:contact.mobilePhone forKey:kMobilePhonKey];
        }
    }
    
    return arrayPhones;
}

- (void)imageFromContactBookWithEmail:(NSString *)anEmail people:(CNContactStore *)aPeople withImageBlock:(void(^) (UIImage *image))block {
    if (anEmail == nil || anEmail.length == 0 || [anEmail isEqualToString:[UserDefaults userCredential]]) {
        block(nil);
        return;
    }
    
    if([[UserDefaults userMemberID] isEqualToString:AllAccounts]) {
        UserEntity *user = [UserEntity loadUserInfoForEmail:anEmail].firstObject;
        if (user) {
            block(nil);
            return;
        }
    }
    
    __block UIImage *image = [ContactsEntity imageFromDomainAvatarsForEmail:anEmail];
    
    if (image) {
        block(image);
    }
    
    if (aPeople == nil) {
        block(nil);
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
        [self getUserFromAddressBookWithEmail:anEmail people:aPeople withContactBlock:^(CNContact *cnContact) {
             if (cnContact.imageDataAvailable) {
                 image = [UIImage imageWithData:cnContact.thumbnailImageData];
                 block(image);
             }
             else {
                 block(nil);
             }
         }];
    });
}

- (UIImage *)imageFromContactBookWithEmail:(NSString *)anEmail people:(CNContactStore *)aPeople {
    if (anEmail == nil || anEmail.length == 0 || [anEmail isEqualToString:[UserDefaults userCredential]]) {
        return nil;
    }
    
    if([[UserDefaults userMemberID] isEqualToString:AllAccounts]) {
        UserEntity *user = [UserEntity loadUserInfoForEmail:anEmail].firstObject;
        if (user) {
            return nil;
        }
    }
    
    UIImage *image = [ContactsEntity imageFromDomainAvatarsForEmail:anEmail];
    
    if (image) {
        return image;
    }

    CNContact *abPerson = [self getUserFromAddressBookWithEmail:anEmail people:aPeople];
    if (abPerson.imageDataAvailable) {
        image = [UIImage imageWithData:abPerson.thumbnailImageData];
    }
    
    return image;
}

#pragma mark - Add contact to Contacts Store

- (void)addContactToContactStore:(NSArray *)emails {
    [ContactStore createAddressBookWithCompletionBlock:^(CNContactStore *theAddressBook, NSArray *thePeopleFromAddressBook) {
        for (NSString *contactEmail in emails) {
            ContactSyncEntity *contact = [[ContactSyncEntity alloc] init];
            contact.firstName = [contactEmail componentsSeparatedByString:@"@"].firstObject;
            contact.workEmail = contactEmail;
            [self addContact:contact toContactStore:theAddressBook];
        }
    }];
}

- (void)addContact:(ContactSyncEntity *)entity toContactStore:(CNContactStore *)contactStore {
    CNMutableContact *contact = [[CNMutableContact alloc] init];
    if(entity.firstName) {
        contact.givenName = entity.firstName;
    }
    if(entity.lastName) {
        contact.familyName = entity.lastName;
    }
    if(entity.middleName) {
        contact.middleName = entity.middleName;
    }
    if(entity.nickname) {
        contact.nickname = entity.nickname;
    }
    if(entity.personalNote) {
        contact.note = entity.personalNote;
    }
    if(entity.suffix) {
        contact.nameSuffix = entity.suffix;
    }
    
    NSDateFormatter *dateFormatBirth = [[NSDateFormatter alloc] init];
    [dateFormatBirth setTimeZone:[NSTimeZone timeZoneWithName: @"GMT"]];
    [dateFormatBirth setDateFormat:@"MM/dd/yyyy"];
    NSDate *dateBirth = [dateFormatBirth dateFromString:entity.birthday];
    NSString *dateString = [dateFormatBirth stringFromDate:dateBirth];
    NSArray *dates = [dateString componentsSeparatedByString:@"/"];
    NSDateComponents *birthdayComponents = [[NSDateComponents alloc] init];
    if(dates.count > 0) {
        birthdayComponents.month = [dates[0] integerValue];
        birthdayComponents.day = [dates[1] integerValue];
        birthdayComponents.year = [dates[2] integerValue];
        
        contact.birthday = birthdayComponents;
    }
    
    if(entity.company) {
        contact.organizationName = entity.company;
    }
    if(entity.workTitle) {
        contact.jobTitle = entity.workTitle;
    }
    if(entity.title) {
        contact.namePrefix = entity.title;
    }
    
    NSMutableArray<CNLabeledValue<CNPhoneNumber*>*> *phoneNumbers = [NSMutableArray new];
    if(entity.homePhone) {
        CNLabeledValue *homePhoneValue = [CNLabeledValue labeledValueWithLabel:CNLabelHome value:[CNPhoneNumber phoneNumberWithStringValue:entity.homePhone]];
        [phoneNumbers addObject:homePhoneValue];
    }
    if(entity.homeFax) {
        CNLabeledValue *homeFaxLabel = [CNLabeledValue labeledValueWithLabel:CNLabelPhoneNumberHomeFax value:[CNPhoneNumber phoneNumberWithStringValue:entity.homeFax]];
        [phoneNumbers addObject:homeFaxLabel];
    }
    if(entity.mobilePhone) {
        CNLabeledValue *mobilePhoneLabel = [CNLabeledValue labeledValueWithLabel:CNLabelPhoneNumberMobile value:[CNPhoneNumber phoneNumberWithStringValue:entity.mobilePhone]];
        [phoneNumbers addObject:mobilePhoneLabel];
    }
    if(entity.workPhone) {
        CNLabeledValue *workPhoneLabel = [CNLabeledValue labeledValueWithLabel:CNLabelWork value:[CNPhoneNumber phoneNumberWithStringValue:entity.workPhone]];
        [phoneNumbers addObject:workPhoneLabel];
    }
    if(entity.workFax) {
        CNLabeledValue *workFaxLabel = [CNLabeledValue labeledValueWithLabel:CNLabelPhoneNumberWorkFax value:[CNPhoneNumber phoneNumberWithStringValue:entity.workFax]];
        [phoneNumbers addObject:workFaxLabel];
    }
    if(phoneNumbers.count > 0) {
        contact.phoneNumbers = phoneNumbers;
    }
    
    
    NSMutableArray<CNLabeledValue<NSString*>*> *emails = [NSMutableArray new];
    if(entity.homeEmail) {
        CNLabeledValue *homeEmailLabel = [CNLabeledValue labeledValueWithLabel:CNLabelHome value:entity.homeEmail];
        [emails addObject:homeEmailLabel];
    }
    if(entity.homeEmail2) {
        CNLabeledValue *homeEmail2Label = [CNLabeledValue labeledValueWithLabel:CNLabelHome value:entity.homeEmail2];
        [emails addObject:homeEmail2Label];
    }
    if(entity.webEmail) {
        CNLabeledValue *webEmailLabel = [CNLabeledValue labeledValueWithLabel:CNLabelHome value:entity.webEmail];
        [emails addObject:webEmailLabel];
    }
    if(entity.workEmail) {
        CNLabeledValue *workEmailLabel = [CNLabeledValue labeledValueWithLabel:CNLabelWork value:entity.workEmail];
        [emails addObject:workEmailLabel];
    }
    if(entity.workEmail2) {
        CNLabeledValue *workEmail2Label = [CNLabeledValue labeledValueWithLabel:CNLabelWork value:entity.workEmail2];
        [emails addObject:workEmail2Label];
    }
    if(emails.count > 0) {
        contact.emailAddresses = emails;
    }
    
    
    NSMutableArray<CNLabeledValue<NSString*>*> *urls = [NSMutableArray new];
    if(entity.personalWebSite) {
        CNLabeledValue *personalWebSiteLabel = [CNLabeledValue labeledValueWithLabel:CNLabelURLAddressHomePage value:entity.personalWebSite];
        [urls addObject:personalWebSiteLabel];
    }
    if(entity.workWebSite) {
        CNLabeledValue *workWebSiteLabel = [CNLabeledValue labeledValueWithLabel:CNLabelWork value:entity.workWebSite];
        [urls addObject:workWebSiteLabel];
    }
    if(entity.webSite) {
        CNLabeledValue *webSiteLabel = [CNLabeledValue labeledValueWithLabel:CNLabelHome value:entity.webSite];
        [urls addObject:webSiteLabel];
    }
    if(urls.count > 0) {
        contact.urlAddresses = urls;
    }
    
    
    CNMutablePostalAddress *homeAddress = nil;
    if([self isContactHasHomeAddress:entity]) {
        homeAddress = [[CNMutablePostalAddress alloc] init];
        if (entity.homeStreet) {
            homeAddress.street = entity.homeStreet;
        }
        if (entity.homeStreet2) {
            homeAddress.street = entity.homeStreet2;
        }
        if (entity.homeCity){
            homeAddress.city = entity.homeCity;
        }
        if (entity.homeState) {
            homeAddress.state = entity.homeState;
        }
        if (entity.homeCountry) {
            homeAddress.country = entity.homeCountry;
        }
        if (entity.homePostalCode) {
            homeAddress.postalCode = entity.homePostalCode;
        }
    }

    CNMutablePostalAddress *workAddress = nil;
    if([self isContactHasWorkAddress:entity]) {
        workAddress = [[CNMutablePostalAddress alloc] init];
        if (entity.workStreet) {
            workAddress.street = entity.workStreet;
        }
        if (entity.workStreet2) {
            workAddress.street = entity.workStreet2;
        }
        if (entity.workCity) {
            workAddress.city = entity.workCity;
        }
        if (entity.workState) {
            workAddress.state = entity.workState;
        }
        if (entity.workCountry) {
            workAddress.country = entity.workCountry;
        }
        if (entity.workPostalCode) {
            workAddress.postalCode = entity.workPostalCode;
        }
    }
    
    NSMutableArray *validAddresses = [NSMutableArray array];
    if(homeAddress) {
        CNLabeledValue *home = [CNLabeledValue labeledValueWithLabel:CNLabelHome value:homeAddress];
        [validAddresses addObject:home];
    }
    if(workAddress) {
        CNLabeledValue *work = [CNLabeledValue labeledValueWithLabel:CNLabelWork value:workAddress];
        [validAddresses addObject:work];
    }
    if(validAddresses.count > 0) {
        contact.postalAddresses = validAddresses;
    }
    
    CNSaveRequest *saveRequest = [[CNSaveRequest alloc] init];
    [saveRequest addContact:contact toContainerWithIdentifier:nil];
    
    if(![self isContactExist:contact inContactStore:contactStore]) {;
        NSError *saveError;
        [contactStore executeSaveRequest:saveRequest error:&saveError];
        if(saveError) {
            NSLog(@"Save contact: %@", saveError);
        }
        
        entity.clientID = contact.identifier;
        
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setDateFormat:@"yyyy-MM-dd HH-mm-ss Z"];
        NSString *todayDate = [dateFormat stringFromDate:[NSDate date]];
        NSDate *date = [dateFormat dateFromString:todayDate];
        [entity setRevision:[NSString stringWithFormat:@"%.0f", [date timeIntervalSince1970]]];
        
        [ContactSyncEntity insertContactsSyncToDatabase:entity andCallResultQuery:^(NSError *error, int errorCode, NSString *errorMessage) {
        }];
    }
}

#pragma mark - Validation methods

- (BOOL)isContactHasHomeAddress:(ContactSyncEntity *)contact {
    if(contact.homeStreet || contact.homeStreet2 || contact.homeCity || contact.homeState || contact.homePostalCode || contact.homeCountry) {
        return YES;
    }
    return NO;
}

- (BOOL)isContactHasWorkAddress:(ContactSyncEntity *)contact {
    if(contact.workStreet || contact.workStreet2 || contact.workCity || contact.workState || contact.workPostalCode || contact.workCountry) {
        return YES;
    }
    return NO;
}

- (BOOL)isContactExist:(CNContact *)toCompareContact inContactStore:(CNContactStore *)contactStore {
    __block BOOL isExistContact = NO;
    NSArray *keys = @[CNContactFamilyNameKey, CNContactGivenNameKey, CNContactPhoneNumbersKey, CNContactImageDataKey, CNContactEmailAddressesKey];
    CNContactFetchRequest *request = [[CNContactFetchRequest alloc] initWithKeysToFetch:keys];
    NSError *error;
    [contactStore enumerateContactsWithFetchRequest:request error:&error usingBlock:^(CNContact * __nonnull contact, BOOL * __nonnull stop) {
        if (error) {
            NSLog(@"error fetching contacts %@", error);
        } else {
            NSArray<CNLabeledValue<NSString*>*> *toCompareEmails = toCompareContact.emailAddresses;
            NSArray<CNLabeledValue<NSString*>*> *foundEmails = contact.emailAddresses;
            if(toCompareEmails.count == foundEmails.count) {
                for (CNLabeledValue<NSString*> *email in toCompareEmails) {
                    isExistContact = [foundEmails containsObject:email];
                }
            }
        }
    }];
    
    return isExistContact;
}

#pragma mark - Contacts store was updated notification selector

- (void)contactStoreWasUpdated {
    self.cnContacts = nil;
}

@end
