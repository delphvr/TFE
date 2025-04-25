import 'package:calendar_app/organizer/project/new_project.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('nouveau project form', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('new projet with missing name', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: NewProjectPage(
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(NewProjectPage), findsOneWidget);

      await tester.enterText(find.byKey(const Key('description')),
          'Spectacle de danse et de chant');
      await tester.tap(find.byKey(const Key('beginningDate')),
          warnIfMissed: false);
      await tester.pumpAndSettle();
      await tester.tap(find.text('1'));
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(const Key('endingDate')),
          warnIfMissed: false);
      await tester.pumpAndSettle();
      final endingDate = DateTime.now();
      await tester.tap(find.text(endingDate.day.toString()));
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Enregistrer'));
      await tester.pumpAndSettle();
      expect(find.text('Veuillez donner un nom au project.'), findsOneWidget);
    });

    testWidgets('new projet with missing begining date',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: NewProjectPage(
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(NewProjectPage), findsOneWidget);

      await tester.enterText(find.byKey(const Key('name')), 'Aladin');
      await tester.enterText(find.byKey(const Key('description')),
          'Spectacle de danse et de chant');
      await tester.tap(find.byKey(const Key('endingDate')),
          warnIfMissed: false);
      await tester.pumpAndSettle();
      final endingDate = DateTime.now();
      await tester.tap(find.text(endingDate.day.toString()));
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Enregistrer'));
      await tester.pumpAndSettle();
      expect(find.text('Veuillez donner une date de début au project.'),
          findsOneWidget);
    });

    testWidgets('new projet with missing ending date',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: NewProjectPage(
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(NewProjectPage), findsOneWidget);

      await tester.enterText(find.byKey(const Key('name')), 'Aladin');
      await tester.enterText(find.byKey(const Key('description')),
          'Spectacle de danse et de chant');
      await tester.tap(find.byKey(const Key('beginningDate')),
          warnIfMissed: false);
      await tester.pumpAndSettle();
      await tester.tap(find.text('1'));
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Enregistrer'));
      await tester.pumpAndSettle();
      expect(find.text('Veuillez donner une date de fin au project.'),
          findsOneWidget);
    });

    testWidgets('new projet with enging date before begining date',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: NewProjectPage(
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(NewProjectPage), findsOneWidget);

      await tester.enterText(find.byKey(const Key('name')), 'Aladin');
      await tester.enterText(find.byKey(const Key('description')),
          'Spectacle de danse et de chant');
      await tester.tap(find.byKey(const Key('beginningDate')),
          warnIfMissed: false);
      await tester.pumpAndSettle();
      if (DateTime.now().add(const Duration(days: 1)).day != 1) {
        DateTime beginningDate = DateTime.now().add(const Duration(days: 1));
        await tester.tap(find.text(beginningDate.day.toString()));
        await tester.tap(find.text('OK'));
        await tester.pumpAndSettle();
        await tester.tap(find.byKey(const Key('endingDate')),
            warnIfMissed: false);
        await tester.pumpAndSettle();
        final endingDate = DateTime.now();
        await tester.tap(find.text(endingDate.day.toString()));
        await tester.tap(find.text('OK'));
        await tester.pumpAndSettle();
        await tester.tap(find.text('Enregistrer'));
        await tester.pumpAndSettle();
        expect(find.text('La date de fin du projet ne peut pas avoir lieu avant la date de début.'), findsOneWidget);
      }
    });

    testWidgets('new projet with enging date in the past',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: NewProjectPage(
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(NewProjectPage), findsOneWidget);

      await tester.enterText(find.byKey(const Key('name')), 'Aladin');
      await tester.enterText(find.byKey(const Key('description')),
          'Spectacle de danse et de chant');
      await tester.tap(find.byKey(const Key('beginningDate')),
          warnIfMissed: false);
      await tester.pumpAndSettle();
      if (DateTime.now().add(const Duration(days: 1)).day != 1) {
        await tester.tap(find.text('1'));
        await tester.tap(find.text('OK'));
        await tester.pumpAndSettle();
        await tester.tap(find.byKey(const Key('endingDate')),
            warnIfMissed: false);
        await tester.pumpAndSettle();
        await tester.tap(find.text('1'));
        await tester.tap(find.text('OK'));
        await tester.pumpAndSettle();
        await tester.tap(find.text('Enregistrer'));
        await tester.pumpAndSettle();
        expect(find.text('La date de fin du projet ne peut pas avoir lieu dans le passé.'), findsOneWidget);
      }
    });
  });
}
