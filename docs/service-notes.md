# Atelier 4 : Couche Service, IoC et Injection de Dépendances

## A. Modes d'injection comparés

| Critère | Injection par constructeur | Injection par attribut (`@Autowired`) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | Oui (garantit l'immuabilité) | Non |
| **Dépendance visible dans la signature ?** | Oui (visible via les paramètres du constructeur) | Non (dépendance cachée) |
| **Utilisation hors conteneur (ex: tests unitaires avec `new`) ?** | Oui, directement instanciable en passant les mocks | Difficile, nécessite réflexion ou conteneur (risque de `NullPointerException`) |
| **Signalé par SonarQube / SonarLint ?** | Non (bonne pratique recommandée) | Oui (règle S6813 : « Use constructor injection ») |

---

## B. Couche Service et Dépendances Injectées

| Service | Dépendances injectées | Mode d'injection | Justification |
| :--- | :--- | :--- | :--- |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, lecture seule, injection explicite par interface |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Composant sans état, injection explicite par interface, champ `final` |

---

## C. Diagnostic des Messages d'Erreur du Conteneur

### Message 1 : `NoSuchBeanDefinitionException` sur `IContratService`
- **Cause 1** : L'annotation `@Service` (ou `@Component`) a été omise sur la classe `ContratServiceImpl`.
- **Cause 2** : La classe d'implémentation est située dans un package non scanné par `@SpringBootApplication` (en dehors de `tn.esprit.autoloc`).
- **Correction** : Ajouter `@Service` sur `ContratServiceImpl` et s'assurer qu'elle est bien dans le package scanné `tn.esprit.autoloc.service.impl` ou `tn.esprit.autoloc.service`.

### Message 2 : `NoUniqueBeanDefinitionException` (ambiguïté de beans)
- **Correction 1** : Placer `@Primary` sur la classe de l'implémentation préférentielle (ex: sur `@Service @Primary public class EmailNotificateur ...`).
- **Correction 2** : Utiliser `@Qualifier("emailNotificateur")` au niveau du paramètre du constructeur qui injecte la dépendance.

### Message 3 : Dépendance circulaire (`BeanCurrentlyInCreationException`)
- **Pourquoi l'injection par constructeur rend-elle ce cycle impossible ?** Pour instancier `ClientServiceImpl`, Spring doit d'abord instancier `ReservationServiceImpl`, qui à son tour a besoin de `ClientServiceImpl` avant d'être créé. Aucun des deux constructeurs ne peut donc terminer son exécution.
- **Piste de conception pour le supprimer** : Revoir les responsabilités métier (extraire la logique commune ou croisée dans un troisième service médiateur ou passer par des événements applicatifs / repositories directs sans couplage circulaire).

---

## D. Anomalies SonarQube for IDE (SonarLint)

| Anomalie | Règle / Explication | Correction apportée |
| :--- | :--- | :--- |
| Injection par attribut (`@Autowired private IContratRepository contratRepository;`) | **S6813** : L'injection par champ cache les dépendances et empêche l'immuabilité (`final`). | Remplacée par l'injection par constructeur via `@RequiredArgsConstructor` et champ `final`. |
| Lever des exceptions génériques (`throw new RuntimeException(...)`) | **S112** : Les exceptions génériques masquent la nature réelle de l'erreur métier et compliquent la capture fine. | Remplacée par une exception métier explicite `ResourceNotFoundException` ou `IllegalArgumentException`. |
| Utilisation de `@Data` sur les entités JPA | Règle SonarQube / Clean Code : Génère `equals`, `hashCode` et `toString` incluant toutes les associations circulaires (risque de `StackOverflowError` et requêtes N+1 non voulues). | Remplacée par des annotations ciblées Lombok : `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`. |

---

## E. Questions de Compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**  
   Il n'y a aucun `new ContratServiceImpl` explicite dans le code de l'application. C'est le conteneur Spring IoC (`ApplicationContext`) qui instancie le bean par réflexion lors du démarrage de l'application pendant la phase d'instanciation des composants scannés.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**  
   Pour respecter le principe d'inversion des dépendances (DIP des principes SOLID). Cela garantit un couplage faible entre le contrôleur et la couche métier, facilitant la maintenabilité, l'évolution et les tests unitaires via des mocks.

3. **Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.**  
   Dans Spring, par défaut, un service est un singleton partagé par tous les threads traitant les requêtes HTTP concurrentes. S'il possède un attribut modifiable (comme `private Contrat contratCourant;`), une requête HTTP peut écraser les données d'un autre utilisateur en plein traitement (concurrence / corruption de données).

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**  
   Passer directement l'objet reçu écraserait toutes les colonnes non renseignées dans la requête (les mettant à `null`). De plus, pour les associations gérées avec `cascade` ou `orphanRemoval` (ex: la liste des paiements), cela risquerait de détruire involontairement les données associées en base.

5. **Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**  
   - `@Component` s'applique sur une classe pour en faire un bean détecté automatiquement par le component scanning. `@Bean` s'applique sur une méthode d'une classe `@Configuration` pour enregistrer explicitement une instance dans le conteneur.
   - `@Primary` définit le bean candidat prioritaire par défaut en cas d'ambiguïté. `@Qualifier` permet au consommateur de nommer explicitement le bean exact à injecter.
   - `IPaiementService` est en lecture seule car dans les règles métier d'AutoLoc, un paiement fait partie intégrante du cycle de vie d'un `Contrat` (composition forte). Les créations, modifications ou suppressions de paiements doivent impérativement être orchestrées via le contrat pour assurer l'intégrité financière.
