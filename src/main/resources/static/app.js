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
const feedback = document.getElementById("feedback");
const submitButton = document.getElementById("submit-button");
const resetButton = document.getElementById("reset-button");
const refreshButton = document.getElementById("refresh-button");
const listState = document.getElementById("list-state");
const livresList = document.getElementById("livres-list");
const bookCount = document.getElementById("book-count");

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
    const payload = {
        titre: formData.get("titre")?.toString().trim(),
        auteur: formData.get("auteur")?.toString().trim(),
        categorie: formData.get("categorie")?.toString().trim(),
        isbn: formData.get("isbn")?.toString().trim()
    };

    submitButton.disabled = true;
    afficherMessage("Enregistrement du livre...", "success");

    try {
        const data = await envoyerJson("/api/livres", payload);
        form.reset();
        afficherMessage(`Livre "${data.titre}" ajoute avec succes.`, "success");
        await chargerLivres();
    } catch (error) {
        afficherMessage(error.message, "error");
    } finally {
        submitButton.disabled = false;
    }
});

resetButton.addEventListener("click", () => {
    form.reset();
    afficherMessage("", "");
});

refreshButton.addEventListener("click", () => {
    chargerLivres();
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
    if (charger) {
        chargerLivres();
    }
}

async function chargerLivres() {
    listState.textContent = "Chargement des livres...";
    listState.classList.remove("hidden");
    livresList.innerHTML = "";

    try {
        const response = await fetch("/api/livres");
        const livres = await response.json();

        if (!response.ok) {
            throw new Error("Impossible de recuperer la liste des livres.");
        }

        renderLivres(livres);
    } catch (error) {
        bookCount.textContent = "Erreur";
        listState.textContent = error.message;
        livresList.innerHTML = "";
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
        </li>
    `).join("");
}

async function envoyerJson(url, payload) {
    const response = await fetch(url, {
        method: "POST",
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
