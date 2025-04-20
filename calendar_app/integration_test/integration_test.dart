import 'package:calendar_app/auth/login.dart';
import 'package:calendar_app/organizer/project/project_admin.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:calendar_app/main.dart' as app;
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:mockito/annotations.dart';

//befor running this test make shure the backend is running

@GenerateMocks([http.Client])
void main() {
  //IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  //https://stackoverflow.com/questions/65412897/new-integration-test-package-just-shows-test-starting-android
  final binding = IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  binding.framePolicy = LiveTestWidgetsFlutterBindingFramePolicy.fullyLive;

  setUp(() async {
    await dotenv.load(fileName: "lib/.env");
  });

  testWidgets('test register and create a project',
      (WidgetTester tester) async {
    app.main();
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 2));

    expect(find.text('Créer un compte'), findsOneWidget);

    await tester.tap(find.text('Créer un compte'));
    await tester.pumpAndSettle();

    await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
    await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
    await tester.enterText(
        find.byKey(const Key('emailField')), 'test1@mail.com');
    await tester.enterText(find.byKey(const Key('passwordField')), 'password');
    await tester.enterText(
        find.byKey(const Key('confirmPasswordField')), 'password');
    await Future.delayed(const Duration(seconds: 2));

    await tester.tap(find.text('Créer mon compte'));
    await tester.pumpAndSettle();

    //debugDumpApp();
    await Future.delayed(const Duration(seconds: 2));

    expect(find.byType(ProjectOrganizerPage), findsOneWidget);
    //TODO creer un projet

    await tester.tap(find.text('Profil'));

    await Future.delayed(const Duration(seconds: 2));

    expect(find.text("Nom : Pley"), findsOneWidget);
    expect(find.text("Prénom : Eve"), findsOneWidget);
    expect(find.text("email : test1@mail.com"), findsOneWidget);
    expect(find.text("Professions : -"), findsOneWidget);

    await tester.tap(find.text('Supprimer mon compte'));
    await Future.delayed(const Duration(seconds: 2));
    await tester.tap(find.text('oui'));
    expect(find.byType(LoginScreen), findsOneWidget);
    await Future.delayed(const Duration(seconds: 2));
  });
}
