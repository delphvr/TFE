import 'package:calendar_app/organizer/rehearsals/add_rehearsal.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsals.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('rehearsals', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('rehearsal list', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: RehearsalPage(
          projectId: 1,
          projectName: 'Aladin',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(RehearsalPage), findsOneWidget);
      expect(find.text("Ajouter une répétition"), findsOneWidget);
      expect(find.text("Répétition générale"), findsOneWidget);
      expect(find.text("Description : Dernière répétition avec tous le monde"), findsOneWidget);
      expect(find.text("Durée : 5h"), findsOneWidget);
      expect(find.text("Chorégraphie"), findsOneWidget);
      expect(find.text("Durée : 3h"), findsOneWidget);
      expect(find.text("Petite répétition"), findsOneWidget);
      expect(find.text("Durée : 2h"), findsOneWidget);
    });

    testWidgets('create rehearsal with missing name', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: AddRehearsal(
          projectId: 1,
          projectName: 'Aladin',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(AddRehearsal), findsOneWidget);
      expect(find.text("Durée *"), findsOneWidget);
      await tester.tap(find.text('Durée *'), warnIfMissed: false);
      await tester.pumpAndSettle();
      await tester.tap(find.text('2'));
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      expect(find.text("Ajouter"), findsOneWidget);
      await tester.ensureVisible(find.text('Ajouter'));
      await tester.tap(find.text('Ajouter'));
      await tester.pumpAndSettle();
      expect(find.text("Merci de donner un nom à la répétition"), findsOneWidget);
    });

    testWidgets('create rehearsal with missing duration', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: AddRehearsal(
          projectId: 1,
          projectName: 'Aladin',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(AddRehearsal), findsOneWidget);
      await tester.enterText(find.byKey(const Key('nameField')), 'R1');
      await tester.pumpAndSettle();
      expect(find.text("Ajouter"), findsOneWidget);
      await tester.ensureVisible(find.text('Ajouter'));
      await tester.tap(find.text('Ajouter'));
      await tester.pumpAndSettle();
      expect(find.text("Merci de donner une durée à la répétition"), findsOneWidget);
    });
  });
}
