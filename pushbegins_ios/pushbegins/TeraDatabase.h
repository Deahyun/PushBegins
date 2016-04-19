//
//  ETDatabase.h
//
//  Created by Deahyun Kim on 11. 2. 26..
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

@interface TeraDatabase : NSObject {

	NSString *pathToDB;
	
	BOOL logging;
	
	sqlite3 *db;
}

@property (nonatomic, retain) NSString *pathToDB;
@property (nonatomic) BOOL logging;

- (id)initWithPath:(NSString *)filePath;
- (id)initWithFileName:(NSString *)fileName;

- (void)open;
- (void)close;
- (void)raiseSqliteException:(NSString *)errorMessage;

- (NSArray *)executeSql:(NSString *)sql;
- (NSArray *)executeSql:(NSString *)sql withParameters:(NSArray *)parameters;
- (NSArray *)executeSql:(NSString *)sql withParameters:(NSArray *)parameters withClassForRow:(Class)rowClass;
- (NSArray *)executeSqlWithParameters:(NSString *)sql, ...;

- (NSArray *)columnNamesForStatement:(sqlite3_stmt *)statement;
- (NSArray *)columnTypesForStatement:(sqlite3_stmt *)statement;
- (int)typeForStatement:(sqlite3_stmt *)statement column:(int)column;

- (int)columnTypeToInt:(NSString *)columnType;

//- (void)copyValuesFromStatement:(sqlite3_stmt *)statement toRow:(NSMutableDictionary *)row
//					  queryInfo:(NSDictionary *)queryinfo columnTypes:(NSArray *)columnTypes
//					columnNames:(NSArray *)columnNames;

- (void)copyValuesFromStatement:(sqlite3_stmt *)statement toRow:(id)row
					  queryInfo:(NSDictionary *)queryinfo columnTypes:(NSArray *)columnTypes
					columnNames:(NSArray *)columnNames;

- (id)valueFromStatement:(sqlite3_stmt *)statement column:(int)column 
			   queryInfo:(NSDictionary *)queryInfo columnTypes:(NSArray *)columnTypes;


- (NSArray *)tables;
- (NSArray *)tableNames;

- (NSArray *)views;
- (NSArray *)viewNames;

- (void)bindArguments:(NSArray *)arguments toStatement:(sqlite3_stmt *)statement 
			queryInfo:(NSDictionary *)queryInfo;

- (NSArray *)columnsForTableName:(NSString *)tableName;

- (NSUInteger)lastInsertRowId;

- (void)beginTransaction;
- (void)commit;
- (void)rollback;


@end
