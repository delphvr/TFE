import 'package:calendar_app/auth/login.dart';
import 'package:calendar_app/organizer/project/project_admin.dart';
import 'package:calendar_app/organizer/project/project_details.dart';
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
    await Future.delayed(const Duration(seconds: 1));

    //create an account
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
    await Future.delayed(const Duration(seconds: 1));

    await tester.tap(find.text('Créer mon compte'));
    await tester.pumpAndSettle();
    await tester.pumpAndSettle();
    await tester.pumpAndSettle();
    //debugDumpApp();
    await Future.delayed(const Duration(seconds: 1));

    expect(find.byType(ProjectOrganizerPage), findsOneWidget);

    await tester.pumpAndSettle();

    // create a project
    await tester.tap(find.text('Nouveau projet'));
    await tester.pumpAndSettle();
    await tester.enterText(find.byKey(const Key('name')), 'Aladin');
    await tester.enterText(
        find.byKey(const Key('description')), 'Spectacle de danse et de chant');

    await tester.tap(find.byKey(const Key('beginningDate')), warnIfMissed: false);
    await tester.pumpAndSettle();
    await tester.tap(find.text('1'));
    await tester.tap(find.text('OK'));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('endingDate')), warnIfMissed: false);
    await tester.pumpAndSettle();
    final endingDate = DateTime.now();
    await tester.tap(find.text(endingDate.day.toString()));
    await tester.tap(find.text('OK'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    await tester.tap(find.text('Enregistrer'));
    await tester.pumpAndSettle();
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));

    expect(find.byType(ProjectOrganizerPage), findsOneWidget);
    await tester.tap(find.text('Aladin'));
    await tester.pumpAndSettle();
    expect(find.byType(ProjectDetailsPage), findsOneWidget);
    expect(find.text("Description : Spectacle de danse et de chant"), findsOneWidget);

    //add a participant
    await tester.tap(find.text('Voir les participants'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Ajouter un participant'));
    await tester.pumpAndSettle();
    await tester.enterText(find.byKey(const Key('emailField')), 'del.vr@mail.com');
    await tester.tap(find.text('Ajouter'));
    await tester.pumpAndSettle();
    await tester.tap(find.byTooltip('Back'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));

    //add a rehearsal
    await tester.tap(find.text('Voir les répétitions'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Ajouter une répétition'));
    await tester.pumpAndSettle();
    await tester.enterText(find.byKey(const Key('nameField')), 'R1');
    await tester.enterText(find.byKey(const Key('descriptionField')), 'Firts rehearsal');
    expect(find.text("Durée *"), findsOneWidget);
    await tester.tap(find.text('Durée *'), warnIfMissed: false);
    await tester.pumpAndSettle();
    await tester.tap(find.text('OK'));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('Participants'));
    await tester.tap(find.text('Participants'));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('Eve Pley'));
    await tester.tap(find.text('Eve Pley'));
    await tester.tap(find.text('Valider'));
    await tester.pumpAndSettle();
    expect(find.text("Ajouter"), findsOneWidget);
    await tester.ensureVisible(find.text('Ajouter'));
    await tester.tap(find.text('Ajouter'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    expect(find.text("Ajouter une répétition"), findsOneWidget);
    expect(find.text("R1"), findsOneWidget);
    expect(find.text("Description : Firts rehearsal"), findsOneWidget);
    await tester.tap(find.text('R1'), warnIfMissed: false);
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    expect(find.text("R1"), findsOneWidget);
    expect(find.text("Description : Firts rehearsal"), findsOneWidget);
    expect(find.text("Date : - "), findsOneWidget);
    expect(find.text("Durée : 2h"), findsOneWidget);
    expect(find.text("Lieu : -"), findsOneWidget);
    expect(find.text("Participants : "), findsOneWidget);
    expect(find.text("Pley Eve"), findsOneWidget);

    await tester.tap(find.byTooltip('Back'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    await tester.tap(find.byTooltip('Back'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));

    //calendar proposition
    await tester.tap(find.text('Calculer l\'horaire'));
    await tester.pumpAndSettle();
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    expect(find.text("R1"), findsOneWidget);
    expect(find.text("0/1"), findsOneWidget);
    await tester.tap(find.text('Tout valider'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));

    //See the calendar
    await tester.tap(find.text('Calendrier'));
    await tester.pumpAndSettle();
    expect(find.text("R1"), findsOneWidget);

    //delete the project
    await tester.tap(find.text('Organisateurs'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Aladin'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Supprimer le projet'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('oui'));
    await tester.pumpAndSettle();
    await tester.pumpAndSettle();
    expect(find.byType(ProjectOrganizerPage), findsOneWidget);

    //Delete the account
    await tester.tap(find.text('Profil'));
    await tester.pumpAndSettle();

    await Future.delayed(const Duration(seconds: 1));

    expect(find.text("Nom : Pley"), findsOneWidget);
    expect(find.text("Prénom : Eve"), findsOneWidget);
    expect(find.text("email : test1@mail.com"), findsOneWidget);
    expect(find.text("Professions : -"), findsOneWidget);

    await tester.tap(find.text('Supprimer mon compte'));
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    await tester.tap(find.text('oui'));
    await tester.pumpAndSettle();
    await tester.pumpAndSettle();
    await Future.delayed(const Duration(seconds: 1));
    expect(find.byType(LoginScreen), findsOneWidget);
  });
}
/*
DELETE FROM users
WHERE email = 'test1@mail.com';
*/