import 'package:calendar_app/project/project_user.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:calendar_app/main.dart';
import 'package:flutter/material.dart';
import 'package:mockito/mockito.dart';
import 'package:http/http.dart' as http;
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:mockito/annotations.dart';
import 'dart:convert';

import 'integration_test.mocks.dart';

@GenerateMocks([http.Client, FirebaseAuth, User, UserCredential])
void main() {
  //IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  late MockClient client;
  late MockFirebaseAuth mockAuth;
  late MockUserCredential mockUserCredential;

  setUp(() async {
    await dotenv.load(fileName: "lib/.env");
    client = MockClient();
    mockAuth = MockFirebaseAuth();
    mockUserCredential = MockUserCredential();

    final mockUser = MockUser();
    when(mockUser.uid).thenReturn('test-uid');
    when(mockUser.email).thenReturn('eve@mail.com');
    when(mockUser.displayName).thenReturn('Eve Pley');
    when(mockAuth.currentUser).thenReturn(null);
    
    when(mockUserCredential.user).thenReturn(null);
    when(mockAuth.authStateChanges()).thenAnswer((_) => Stream.value(null));

    when(client.get(Uri.parse('${dotenv.env['API_BASE_URL']}/professions')))
        .thenAnswer((_) async => http.Response('''[
          {"profession": "Danseur"},
          {"profession": "Comédien"},
          {"profession": "Musicien"}
        ]''', 200));

    when(client.post(
      Uri.parse('${dotenv.env['API_BASE_URL']}/users'),
      headers: anyNamed('headers'),
      body: anyNamed('body'),
    )).thenAnswer((invocation) async {
      final body = jsonDecode(invocation.namedArguments[#body] as String);
      if (body['email'] == 'eve@mail.com' &&
          body['firstName'] == 'Eve' &&
          body['lastName'] == 'Pley') {
        return http.Response(jsonEncode({'id': 1}), 201);
      } else {
        return http.Response('Invalid Data', 400);
      }
    });

    when(mockAuth.createUserWithEmailAndPassword(
      email: anyNamed('email'),
      password: anyNamed('password'),
    )).thenAnswer((_) async {
      when(mockUserCredential.user).thenReturn(mockUser);
      when(mockAuth.authStateChanges()).thenAnswer((_) => Stream.value(mockUser));
      when(mockAuth.currentUser).thenReturn(mockUser);
      return mockUserCredential;
    });
  });

  testWidgets('test register and create a project', (WidgetTester tester) async {
    
    await tester.pumpWidget(MaterialApp(
        home: MyApp(client: client, auth: mockAuth,
),
      ));
    await tester.pumpAndSettle();

    debugDumpApp();

    expect(find.text('Créer un compte'), findsOneWidget);
    
    await tester.tap(find.text('Créer un compte'));
    await tester.pumpAndSettle();

    await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
    await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
    await tester.enterText(find.byKey(const Key('emailField')), 'eve@mail.com');
    await tester.enterText(find.byKey(const Key('passwordField')), 'password');
    await tester.enterText(find.byKey(const Key('confirmPasswordField')), 'password');

    expect(find.text('Créer un compte'), findsOneWidget);

    await tester.tap(find.text('Créer mon compte'));
    await tester.pumpAndSettle();

    expect(find.byType(ProjectsUserPage), findsOneWidget);
  });
}