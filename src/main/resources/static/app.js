const sessionKey = "bibliothequeUtilisateur";

const authShell = document.getElementById("auth-shell");
const libraryShell = document.getElementById("library-shell");
const authFeedback = document.getElementById("auth-feedback");
const loginForm = document.getElementById("login-form");
const registerForm = document.getElementById("register-form");
const loginButton = document.getElementById("login-button");
const registerButton = document.getElementById("register-button");
const authTabs = Array.from(document.querySelectorAll(".auth-tab"));
const authPanels = Array.from(document.querySelectorAll("[data-auth-panel]"));

const currentUserName = document.getElementById("current-user-name");
const currentUserEmail = document.getElementById("current-user-email");
const logoutButton = document.getElementById("logout-button");

const form = document.getElementById("livre-form");
const formTitle = document.getElementById("form-title");
const formSubtitle = document.getElementById("form-subtitle");
const feedback = document.getElementById("feedback");
const submitButton = document.getElementById("submit-button");
const cancelEditButton = document.getElementById("cancel-edit-button");
const resetButton = document.getElementById("reset-button");
const refreshButton = document.getElementById("refresh-button");
const listState = document.getElementById("list-state");
const livresList = document.getElementById("livres-list");
const bookCount = document.getElementById("book-count");
const loanState = document.getElementById("loan-state");
const empruntsList = document.getElementById("emprunts-list");
const loanCount = document.getElementById("loan-count");

let refreshTimer = null;
let livreEnEditionId = null;
let livresCourants = [];
let empruntsCourants = [];

document.addEventListener("DOMContentLoaded", () => {
    initialiserAuthTabs();
    restaurerSession();
});

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(loginForm);
    const payload = {
        email: formData.get("email")?.toString().trim(),
        motDePasse: formData.get("motDePasse")?.toString().trim()
    };

    loginButton.disabled = true;
    afficherAuthMessage("Connexion en cours...", "success");

    try {
        const utilisateur = await envoyerJson("/api/auth/login", payload);
        connecterUtilisateur(utilisateur);
        loginForm.reset();
        afficherAuthMessage(`Bienvenue ${utilisateur.nom}.`, "success");
    } catch (error) {
        afficherAuthMessage(error.message, "error");
    } finally {
        loginButton.disabled = false;
    }
});

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(registerForm);
    const payload = {
        nom: formData.get("nom")?.toString().trim(),
        email: formData.get("email")?.toString().trim(),
        adresse: formData.get("adresse")?.toString().trim(),
        telephone: formData.get("telephone")?.toString().trim(),
        motDePasse: formData.get("motDePasse")?.toString().trim()
    };

    registerButton.disabled = true;
    afficherAuthMessage("Creation du compte en cours...", "success");

    try {
        const utilisateur = await envoyerJson("/api/auth/register", payload);
        connecterUtilisateur(utilisateur);
        registerForm.reset();
        afficherAuthMessage(`Compte cree pour ${utilisateur.nom}.`, "success");
    } catch (error) {
        afficherAuthMessage(error.message, "error");
    } finally {
        registerButton.disabled = false;
    }
});

logoutButton.addEventListener("click", () => {
    stopActualisation();
    localStorage.removeItem(sessionKey);
    libraryShell.classList.add("hidden");
    authShell.classList.remove("hidden");
    afficherMessage("", "");
    afficherAuthMessage("Vous etes deconnecte.", "success");
    loginForm.reset();
    registerForm.reset();
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const utilisateur = lireUtilisateurCourant();
    if (!utilisateur) {
        afficherMessage("Vous devez etre connecte pour creer un livre.", "error");
        return;
    }

    const payload = {
        titre: formData.get("titre")?.toString().trim(),
        auteur: formData.get("auteur")?.toString().trim(),
        categorie: formData.get("categorie")?.toString().trim(),
        isbn: formData.get("isbn")?.toString().trim(),
        utilisateurId: utilisateur.id,
        dateDebutEmprunt: formData.get("dateDebutEmprunt")?.toString().trim(),
        dateFinEmprunt: formData.get("dateFinEmprunt")?.toString().trim()
    };

    submitButton.disabled = true;
    afficherMessage(livreEnEditionId ? "Modification du livre..." : "Enregistrement du livre...", "success");

    try {
        const modeEdition = livreEnEditionId !== null;
        const data = livreEnEditionId
            ? await envoyerJson(`/api/livres/${livreEnEditionId}/avec-emprunt`, payload, "PUT")
            : await envoyerJson("/api/livres/avec-emprunt", payload);
        reinitialiserFormulaireLivre();
        afficherMessage(
            modeEdition
                ? `Livre "${data.livre.titre}" modifie avec succes.`
                : `Livre "${data.livre.titre}" ajoute et emprunte avec succes.`,
            "success"
        );
        await chargerBibliotheque();
    } catch (error) {
        afficherMessage(error.message, "error");
    } finally {
        submitButton.disabled = false;
    }
});

resetButton.addEventListener("click", () => {
    reinitialiserFormulaireLivre();
});

refreshButton.addEventListener("click", () => {
    chargerBibliotheque();
});

cancelEditButton.addEventListener("click", () => {
    reinitialiserFormulaireLivre();
    afficherMessage("Modification annulee.", "success");
});

livresList.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }

    const livreId = Number(button.dataset.id);
    if (!livreId) {
        return;
    }

    if (button.dataset.action === "edit") {
        chargerLivreDansLeFormulaire(livreId);
        return;
    }

    if (button.dataset.action === "delete") {
        const livre = livresCourants.find((item) => item.id === livreId);
        const confirmation = window.confirm(`Supprimer le livre "${livre ? livre.titre : "#" + livreId}" ?`);
        if (!confirmation) {
            return;
        }

        try {
            await envoyerSansCorps(`/api/livres/${livreId}`, "DELETE");
            if (livreEnEditionId === livreId) {
                reinitialiserFormulaireLivre();
            }
            afficherMessage("Livre supprime avec succes.", "success");
            await chargerBibliotheque();
        } catch (error) {
            afficherMessage(error.message, "error");
        }
    }
});

function initialiserAuthTabs() {
    authTabs.forEach((tab) => {
        tab.addEventListener("click", () => {
            const cible = tab.dataset.authTab;
            authTabs.forEach((item) => item.classList.toggle("active", item === tab));
            authPanels.forEach((panel) => panel.classList.toggle("hidden", panel.dataset.authPanel !== cible));
            afficherAuthMessage("", "");
        });
    });
}

function restaurerSession() {
    const sessionBrute = localStorage.getItem(sessionKey);
    if (!sessionBrute) {
        authShell.classList.remove("hidden");
        libraryShell.classList.add("hidden");
        return;
    }

    try {
        const utilisateur = JSON.parse(sessionBrute);
        connecterUtilisateur(utilisateur);
    } catch (error) {
        localStorage.removeItem(sessionKey);
        authShell.classList.remove("hidden");
    }
}

function connecterUtilisateur(utilisateur, charger = true) {
    localStorage.setItem(sessionKey, JSON.stringify(utilisateur));
    currentUserName.textContent = utilisateur.nom;
    currentUserEmail.textContent = utilisateur.email;
    authShell.classList.add("hidden");
    libraryShell.classList.remove("hidden");
    demarrerActualisation();
    if (charger) {
        chargerBibliotheque();
    }
}

function lireUtilisateurCourant() {
    const sessionBrute = localStorage.getItem(sessionKey);
    return sessionBrute ? JSON.parse(sessionBrute) : null;
}

function demarrerActualisation() {
    stopActualisation();
    refreshTimer = setInterval(() => {
        chargerBibliotheque();
    }, 30000);
}

function stopActualisation() {
    if (refreshTimer !== null) {
        clearInterval(refreshTimer);
        refreshTimer = null;
    }
}

async function chargerBibliotheque() {
    listState.textContent = "Chargement de la liste des livres...";
    listState.classList.remove("hidden");
    loanState.textContent = "Chargement des emprunts...";
    loanState.classList.remove("hidden");
    livresList.innerHTML = "";
    empruntsList.innerHTML = "";

    try {
        const [livres, emprunts] = await Promise.all([
            recupererJson("/api/livres"),
            recupererJson("/api/emprunts")
        ]);
        livresCourants = livres;
        empruntsCourants = emprunts;
        renderLivres(livres);
        renderEmprunts(emprunts, livres);
    } catch (error) {
        bookCount.textContent = "Erreur";
        loanCount.textContent = "Erreur";
        listState.textContent = error.message;
        loanState.textContent = error.message;
        livresList.innerHTML = "";
        empruntsList.innerHTML = "";
    }
}

function renderLivres(livres) {
    bookCount.textContent = `${livres.length} livre(s) enregistre(s)`;

    if (livres.length === 0) {
        listState.textContent = "Aucun livre enregistre pour le moment.";
        listState.classList.remove("hidden");
        return;
    }

    listState.classList.add("hidden");

    livresList.innerHTML = livres.map((livre) => `
        <li class="book-item">
            <div class="book-top">
                <div>
                    <h3 class="book-title">${echapperHtml(livre.titre)}</h3>
                    <p class="book-isbn">ISBN: ${echapperHtml(livre.isbn)}</p>
                </div>
                <span class="book-id">#${livre.id}</span>
            </div>
            <div class="book-meta">
                <span class="chip">Auteur: ${echapperHtml(livre.auteur)}</span>
                <span class="chip">Categorie: ${echapperHtml(livre.categorie)}</span>
            </div>
            <div class="item-actions">
                <button type="button" class="secondary action-button" data-action="edit" data-id="${livre.id}">Edit</button>
                <button type="button" class="danger action-button" data-action="delete" data-id="${livre.id}">Delete</button>
            </div>
        </li>
    `).join("");
}

function renderEmprunts(emprunts, livres) {
    loanCount.textContent = `${emprunts.length} emprunt(s) actif(s)`;

    if (emprunts.length === 0) {
        loanState.textContent = "Aucun emprunt actif pour le moment.";
        loanState.classList.remove("hidden");
        return;
    }

    const livresParId = new Map(livres.map((livre) => [livre.id, livre]));
    const utilisateur = lireUtilisateurCourant();
    loanState.classList.add("hidden");

    empruntsList.innerHTML = emprunts.map((emprunt) => {
        const livre = livresParId.get(emprunt.livreId);
        const titreLivre = livre ? livre.titre : `Livre #${emprunt.livreId}`;
        return `
            <li class="book-item loan-item">
                <div class="book-top">
                    <div>
                        <h3 class="book-title">${echapperHtml(titreLivre)}</h3>
                        <p class="book-isbn">Utilisateur: ${echapperHtml(utilisateur ? utilisateur.nom : "Inconnu")}</p>
                    </div>
                    <span class="book-id">Emprunt #${emprunt.id}</span>
                </div>
                <div class="book-meta">
                    <span class="chip">Livre ID: ${emprunt.livreId}</span>
                    <span class="chip">Utilisateur ID: ${emprunt.utilisateurId}</span>
                </div>
                <div class="loan-dates">
                    <span>Debut: ${echapperHtml(emprunt.dateEmprunt)}</span>
                    <span>Fin: ${echapperHtml(emprunt.dateRetour)}</span>
                </div>
            </li>
        `;
    }).join("");
}

async function recupererJson(url) {
    const response = await fetch(url);
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(data.message || "Une erreur est survenue.");
    }

    return data;
}

async function envoyerSansCorps(url, method) {
    const response = await fetch(url, {
        method
    });

    if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(data.message || "Une erreur est survenue.");
    }
}

async function envoyerJson(url, payload, method = "POST") {
    const response = await fetch(url, {
        method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(data.message || "Une erreur est survenue.");
    }

    return data;
}

function chargerLivreDansLeFormulaire(livreId) {
    const livre = livresCourants.find((item) => item.id === livreId);
    if (!livre) {
        afficherMessage("Livre introuvable pour la modification.", "error");
        return;
    }

    const emprunt = empruntsCourants.find((item) => item.livreId === livreId);
    form.elements.titre.value = livre.titre ?? "";
    form.elements.auteur.value = livre.auteur ?? "";
    form.elements.categorie.value = livre.categorie ?? "";
    form.elements.isbn.value = livre.isbn ?? "";
    form.elements.dateDebutEmprunt.value = emprunt?.dateEmprunt ?? "";
    form.elements.dateFinEmprunt.value = emprunt?.dateRetour ?? "";

    livreEnEditionId = livreId;
    formTitle.textContent = "Modifier le livre";
    formSubtitle.textContent = "Mettez a jour le livre et son emprunt actif.";
    submitButton.textContent = "Mettre a jour";
    cancelEditButton.classList.remove("hidden");
    afficherMessage(`Edition du livre "${livre.titre}".`, "success");
    form.scrollIntoView({ behavior: "smooth", block: "start" });
}

function reinitialiserFormulaireLivre() {
    form.reset();
    livreEnEditionId = null;
    formTitle.textContent = "Nouveau livre";
    formSubtitle.textContent = "Ajoutez un livre et son emprunt en meme temps.";
    submitButton.textContent = "Enregistrer";
    cancelEditButton.classList.add("hidden");
    afficherMessage("", "");
}

function afficherMessage(message, type) {
    feedback.textContent = message;
    feedback.className = "feedback";
    if (type) {
        feedback.classList.add(type);
    }
}

function afficherAuthMessage(message, type) {
    authFeedback.textContent = message;
    authFeedback.className = "feedback";
    if (type) {
        authFeedback.classList.add(type);
    }
}

function echapperHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}
