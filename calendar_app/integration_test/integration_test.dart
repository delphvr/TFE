import 'package:calendar_app/project/project_user.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:calendar_app/main.dart' as app;
import 'package:flutter/material.dart';
import 'package:mockito/mockito.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:mockito/annotations.dart';
import 'dart:convert';

import 'integration_test.mocks.dart';

@GenerateMocks([http.Client])
void main() {
  //IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  //https://stackoverflow.com/questions/65412897/new-integration-test-package-just-shows-test-starting-android
  final binding = IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  binding.framePolicy = LiveTestWidgetsFlutterBindingFramePolicy.fullyLive;

  late MockClient client;

  setUp(() async {
    await dotenv.load(fileName: "lib/.env");
    client = MockClient();

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
      if (body['email'] == 'test1@mail.com' &&
          body['firstName'] == 'Eve' &&
          body['lastName'] == 'Pley') {
        return http.Response(jsonEncode({'id': 1}), 201);
      } else {
        return http.Response('Invalid Data', 400);
      }
    });
  });

  testWidgets('test register and create a project', (WidgetTester tester) async {
    
    /*await tester.pumpWidget(MaterialApp(
        home: MyApp(client: client, auth: mockAuth,
),
      ));*/
    app.main(httpClient: client);
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 2));

    debugDumpApp();

    expect(find.text('Créer un compte'), findsOneWidget);
    
    await tester.tap(find.text('Créer un compte'));
    await tester.pumpAndSettle();

    await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
    await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
    await tester.enterText(find.byKey(const Key('emailField')), 'test1@mail.com');
    await tester.enterText(find.byKey(const Key('passwordField')), 'password');
    await tester.enterText(find.byKey(const Key('confirmPasswordField')), 'password');
    await Future.delayed(const Duration(seconds: 2));

    await tester.tap(find.text('Créer mon compte'));
    await tester.pumpAndSettle();

    expect(find.byType(ProjectsUserPage), findsOneWidget);
    //TODO delete test1@mail.com before ending the test
    //TODO creer un projet
  });
}