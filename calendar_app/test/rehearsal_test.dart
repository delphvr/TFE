import 'package:calendar_app/organizer/rehearsals/add_precedence.dart';
import 'package:calendar_app/organizer/rehearsals/add_rehearsal.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_modification.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_precedence_relatoins.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_presences.dart';
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

    testWidgets('rehearsal modification with missing name', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: RehearsalModificationPage(
          projectId: 1,
          rehearsalId: 1,
          name: 'R1',
          description: null,
          date: null,
          time: null,
          duration: 'PT2h',
          participantsIds: const [],
          location: null,
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(RehearsalModificationPage), findsOneWidget);
      await tester.enterText(find.byKey(const Key('nameField')), '');
      await tester.pumpAndSettle();
      await tester.ensureVisible(find.text('Modifier'));
      await tester.tap(find.text('Modifier'));
      await tester.pumpAndSettle();
      expect(find.text("Veuillez donner un nom à la répétition."), findsOneWidget);
    });

    testWidgets('rehearsal precedence display', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: RehearsalPrecedencesPage(
          projectId: 1,
          rehearsalId: 1,
          rehearsalName: 'R1',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(RehearsalPrecedencesPage), findsOneWidget);
      expect(find.text("Gestion des précédences"), findsOneWidget);
      expect(find.text("Répétitions qui doivent précèder la répétition \"R1\" :"), findsOneWidget);
      expect(find.text("Chorégraphie"), findsOneWidget);
      expect(find.text("Répétitions qui doivent avoir lieu après la répétition \"R1\" :"), findsOneWidget);
      expect(find.text("Répétition général"), findsOneWidget);
    });

    testWidgets('add a rehearsal precedence', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: AddPrecedencePage(
          projectId: 1,
          rehearsalId: 1,
          rehearsalName: 'R1',
          rehearsals: const [{
              'id': 4,
              'name': 'Répétition costume',
              'date': null,
              'time': null,
              'duration': 'PT1H',
              'projectId': 1,
              'location': null,
            }],
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(AddPrecedencePage), findsOneWidget);
      await tester.tap(find.text('Répétitions'));
      await tester.pumpAndSettle();
      expect(find.text("Répétition costume"), findsOneWidget);
    });

    testWidgets('rehearsal presences', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: PresencesPage(
          projectId: 1,
          rehearsalId: 1,
          name: 'R1',
          isCalendar: false,
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(PresencesPage), findsOneWidget);
      expect(find.text("Participants présent à la répétition \"R1\" :"), findsOneWidget);
      expect(find.text("Pley Eve"), findsOneWidget);
      expect(find.text("Participants non présent à la répétition \"R1\" :"), findsOneWidget);
      expect(find.text("Bert Jean"), findsOneWidget);
    });

  });
}
