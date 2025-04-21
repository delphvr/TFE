import 'package:calendar_app/auth/register.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

//https://docs.flutter.dev/cookbook/testing/unit/mocking
//https://docs.flutter.dev/cookbook/testing/widget/tap-drag

void main() {
  group('register', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('register with incorrect email', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: Register(
          onTap: () {},
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
      await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
      await tester.enterText(
          find.byKey(const Key('emailField')), 'evemail.com');
      await tester.enterText(
          find.byKey(const Key('passwordField')), 'password');
      await tester.enterText(
          find.byKey(const Key('confirmPasswordField')), 'password');

      await tester.tap(find.text('Créer mon compte'));
      await tester.pumpAndSettle();
      expect(find.text('Email non valide'), findsOneWidget);
      //print("good");
    });

    testWidgets('register with incorrect password (at least 7 char)',
        (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: Register(
          onTap: () {},
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
      await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
      await tester.enterText(
          find.byKey(const Key('emailField')), 'eve@mail.com');
      await tester.enterText(find.byKey(const Key('passwordField')), '12345');
      await tester.enterText(
          find.byKey(const Key('confirmPasswordField')), '12345');

      await tester.tap(find.text('Créer mon compte'));
      await tester.pumpAndSettle();
      //debugDumpApp();
      expect(find.text('Le mot de passe doit faire au moins 6 caractères.'),
          findsOneWidget);
      //print("good");
    });
  });
}
