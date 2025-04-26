import 'package:calendar_app/profil/profil.dart';
import 'package:calendar_app/profil/profil_modification.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('profil', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('profil informations check', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ProfilPage(
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();

      expect(find.byType(ProfilPage), findsOneWidget);

      //debugDumpApp();

      expect(find.text("Nom : Pley"), findsOneWidget);
      expect(find.text("Prénom : Eve"), findsOneWidget);
      expect(find.text("email : test1@mail.com"), findsOneWidget);
      expect(find.text("Professions :"), findsOneWidget);
      expect(find.text("Danseur"), findsOneWidget);
      expect(find.text("Comédien"), findsOneWidget);

      expect(find.text("Modifier"), findsOneWidget);
      expect(find.text("Gérer mes disponibilité"), findsOneWidget);
      expect(find.text("Supprimer mon compte"), findsOneWidget);
    });

    testWidgets('profil modification wrong email format',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ProfileModificationPage(
          id: 1,
          firstname: "Eve",
          lastname: "Pley",
          email: "test1@mail.com",
          professions: const [],
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();
      expect(find.byType(ProfileModificationPage), findsOneWidget);
      expect(find.text("Modification du Compte"), findsOneWidget);
      await tester.enterText(find.byKey(const Key('emailField')), 'del.vr');
      await tester.tap(find.text('Modifier'));
      await tester.pumpAndSettle();
      expect(find.text("Email non valide"), findsOneWidget);
    });

    testWidgets('profil modification emty email',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ProfileModificationPage(
          id: 1,
          firstname: "Eve",
          lastname: "Pley",
          email: "test1@mail.com",
          professions: const [],
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();
      expect(find.byType(ProfileModificationPage), findsOneWidget);
      expect(find.text("Modification du Compte"), findsOneWidget);
      await tester.enterText(find.byKey(const Key('emailField')), '');
      await tester.tap(find.text('Modifier'));
      await tester.pumpAndSettle();
      expect(find.text("Merci de remplir tout les champs"), findsOneWidget);
    });
  });
}
