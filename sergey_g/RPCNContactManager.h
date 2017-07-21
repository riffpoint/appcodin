//
//  RPCNContactManager.h
//
//  Created by AppCodin <info@appcodin.com> on 7/6/16.
//  Copyright © 2016 AppCodin <info@appcodin.com>. All rights reserved.
//

#import <Foundation/Foundation.h>


#pragma mark - Forward declaration

@import Contacts;


@interface RPCNContactManager : NSObject

#pragma mark - Class method

+ (instancetype) sharedInstance;

#pragma mark - Handle access to the phone's contacts

- (void)createAddressBookWithCompletionBlock:(void(^)(CNContactStore *theAddressBook, NSArray* thePeopleFromAddressBook))aCompletionBlock;

#pragma mark - Fetch methods
- (id)getUserFromAddressBookWithEmail:(NSString *)anEmail people:(CNContactStore *)aPeopleFromAddressBook;
- (NSString *)getContactNamesWithEmail:(NSString *)email source:(CNContactStore *)store;
- (NSMutableDictionary *)getPhoneFromContactBookWithEmail:(NSString *)anEmail people:(CNContactStore *)aPeopleFromAddressBook;
- (UIImage *)imageFromContactBookWithEmail:(NSString *)anEmail people:(CNContactStore *)aPeopleFromAddressBook;
- (NSString *)nameFromContactBookWithEmail:(NSString *)email people:(CNContactStore *)aPeopleFromAddressBook;
- (void)nameFromContactBookWithEmail:(NSString*)anEmail people:(CNContactStore *)aPeople withNameBlock:(void(^)(NSString *name))block;
- (void)imageFromContactBookWithEmail:(NSString*)anEmail people:(CNContactStore*)aPeople withImageBlock:(void(^)(UIImage *image))block;

#pragma mark - Add methods

- (void)addContactToContactStore:(NSArray *)emails;

#pragma mark - Validation methods

- (BOOL)isContactExist:(CNContact *)toCompareContact inContactStore:(CNContactStore *)contactStore;

#pragma mark -

@end
