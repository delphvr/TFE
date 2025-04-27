import 'package:calendar_app/organizer/participants/particpipant_modification.dart';
import 'package:calendar_app/organizer/project/project_admin.dart';
import 'package:calendar_app/organizer/project/project_modification.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('admin projects', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('project admin page informations check', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ProjectOrganizerPage(
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();

      expect(find.byType(ProjectOrganizerPage), findsOneWidget);

      expect(find.text("Nouveau projet"), findsOneWidget);
      await tester.tap(find.text('Projets Archivés'));
      await tester.pumpAndSettle();
      //debugDumpApp();
      expect(find.text("Description : Spectacle de danse et de chant"), findsOneWidget);
      expect(find.text("Aladin"), findsOneWidget);
      expect(find.text("Un project de danse"), findsOneWidget);
      expect(find.text("Début : 01-04-2024"), findsOneWidget);
      expect(find.text("Fin : 10-04-2024"), findsOneWidget);
    });

    testWidgets('project modification page missing name', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: UpdateProjectPage(
          id: 1,
          name: 'Aladin',
          description: null,
          beginningDate: '2024-04-01',
          endingDate: '2024-04-10',
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();
      expect(find.byType(UpdateProjectPage), findsOneWidget);
      await tester.enterText(find.byKey(const Key('nameField')), '');
      await tester.ensureVisible(find.text('Sauvegarder'));
      await tester.tap(find.text('Sauvegarder'));
      await tester.pumpAndSettle();
      expect(find.text("Veuillez donner un nom au project."), findsOneWidget);
    });

    testWidgets('participant information page', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ParticpipantModificationPage(
          userId: 1,
          projectId: 1,
          firstName: "Eve",
          lastName: "Pley",
          email: "test1@mail.com",
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();
      expect(find.byType(ParticpipantModificationPage), findsOneWidget);
      expect(find.text("Organisateur"), findsOneWidget);
      expect(find.text("Acteur"), findsOneWidget);
      expect(find.text("Danseur"), findsOneWidget);
      expect(find.text("Modifier les rôles"), findsOneWidget);
      expect(find.text("Supprimer le participant"), findsOneWidget);
    });

  });
}
