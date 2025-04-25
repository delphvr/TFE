import 'package:calendar_app/organizer/participants/add_participant.dart';
import 'package:calendar_app/organizer/participants/participants.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('project participants', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('participant list', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ParticipantsPage(
          projectId: 1,
          name: 'Aladin',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(ParticipantsPage), findsOneWidget);
      //debugDumpApp();
      expect(find.text("Ajouter un participant"), findsOneWidget);
      expect(find.text("Pley Eve"), findsOneWidget);
      expect(find.text("Bert Jean"), findsOneWidget);
    });

    testWidgets('add participant wrong email format', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: AddParticipant(
          projectId: 1,
          projectName: 'Aladin',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(AddParticipant), findsOneWidget);
      await tester.enterText(find.byKey(const Key('emailField')), 'jean');
      await tester.tap(find.text('Ajouter'));
      await tester.pumpAndSettle();
      expect(find.text("Email non valide"), findsOneWidget);
    });

    testWidgets('add participant empty email', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: AddParticipant(
          projectId: 1,
          projectName: 'Aladin',
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();

      expect(find.byType(AddParticipant), findsOneWidget);
      await tester.tap(find.text('Ajouter'));
      await tester.pumpAndSettle();
      expect(find.text("Merci de remplir tous les champs"), findsOneWidget);
    });

  });
}
