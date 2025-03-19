from flask import Flask, request, render_template, redirect, url_for, flash, session
import requests
from datetime import timedelta
#from flask_session import Session


app = Flask(__name__)
app.secret_key = "cheie_secreta"
#app.config['SESSION_TYPE'] = 'filesystem'
app.config['SESSION_PERMANENT'] = True
app.config['PERMANENT_SESSION_LIFETIME'] = timedelta(days=1)
app.config['SESSION_REFRESH_EACH_REQUEST'] = False

app.config['SESSION_COOKIE_PATH'] = '/'
#app.config['SESSION_COOKIE_HTTPONLY'] = True
#app.config['SESSION_COOKIE_SECURE'] = False
#app.config['SESSION_COOKIE_NAME'] = 'my_session'
#Session(app)

KOTLIN_API_BASE = "http://localhost:8080/app"
kotlin_session = requests.Session()
@app.route("/")
def index():
    return render_template("index.html")

@app.route("/register", methods=["GET", "POST"])
def register():
    if request.method == "POST":
        username = request.form.get("username")
        password = request.form.get("password")
        budget = request.form.get("budget")
        payload = {"username": username, "password": password, "budget": budget}
        response = requests.post(f"{KOTLIN_API_BASE}/gestiune/register", json=payload, verify=False)
        if response.status_code == 201:
            flash("Înregistrare reușită.")
            return redirect(url_for("index"))
        else:
            flash("Eroare la înregistrare: " + response.text)
            return redirect(url_for("register"))
    return render_template("register.html")

@app.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        username = request.form.get("username")
        password = request.form.get("password")
        payload = {"username": username, "password": password}
        response = kotlin_session.post(f"{KOTLIN_API_BASE}/gestiune/login", json=payload, verify=False)
        if response.status_code == 202:
            data = response.json()
            account_id = data.get("id")
            session["loggedInUser"] = account_id
            session.permanent = True
            flash("Autentificare reusita.")
            return redirect(url_for("index"))
        else:
            flash("Autentificare esuata: " + response.text)
            return redirect(url_for("login"))
    return render_template("login.html")

@app.route("/get_budget", methods=["GET", "POST"])
def get_budget():
    if "loggedInUser" not in session:
        flash("Autentificare necesara.")
        return redirect(url_for("login"))
    if request.method == "POST":
        member_id = session["loggedInUser"]
        response = kotlin_session.get(f"{KOTLIN_API_BASE}/gestiune/buget/{member_id}", verify=False)
        if response.status_code == 200:
            result = response.json()
            return render_template("result.html", result=result)
        else:
            flash("Eroare: " + response.text)
            return redirect(url_for("index"))
    return render_template("get_budget.html")

@app.route("/add_budget", methods=["GET", "POST"])
def add_budget():
    if request.method == "POST":
        member_id = session["loggedInUser"]
        amount = request.form.get("amount")
        payload = {"amount": amount}
        response = kotlin_session.post(f"{KOTLIN_API_BASE}/gestiune/buget/adauga/{member_id}", json=payload, verify=False)
        if response.status_code == 200:
            flash("Buget adaugat cu succes.")
            return redirect(url_for("get_budget"))
        else:
            flash("Eroare: " + response.text)
            return redirect(url_for("add_budget"))
    return render_template("add_budget.html")

@app.route("/add_expense", methods=["GET", "POST"])
def add_expense():
    if request.method == "POST":
        idMembru = session["loggedInUser"]
        tip = request.form.get("tip")
        numeCheltuiala = request.form.get("numeCheltuiala")
        cost = request.form.get("cost")
        payload = {
            "idMembru": int(idMembru),
            "tip": tip,
            "numeCheltuiala": numeCheltuiala,
            "cost": float(cost)
        }
        response = kotlin_session.post(f"{KOTLIN_API_BASE}/cheltuieli/adauga", json=payload, verify=False)
        if response.status_code == 201:
            flash("Cheltuială adăugată cu succes.")
            return redirect(url_for("index"))
        else:
            flash("Eroare: " + response.text)
            return redirect(url_for("add_expense"))
    return render_template("add_expense.html")

@app.route("/list_expenses", methods=["GET", "POST"])
def list_expenses():
    if "loggedInUser" not in session:
        flash("Autentificare necesara.")
        return redirect(url_for("login"))

    member_id = session["loggedInUser"]
    if request.method == "GET":
        print("intrat in GET, loggedInUser:", member_id)
        response = kotlin_session.get(f"{KOTLIN_API_BASE}/cheltuieli/membru/{member_id}", verify=False)
        if response.status_code == 200:
            expenses = response.json()
            print("success:", expenses)
            return render_template("list_expenses.html", expenses=expenses)
        else:
            flash("Eroare: " + response.text)
            print("Eroare la API, status:", response.status_code)
            return redirect(url_for("list_expenses"))
    print("error")
    return render_template("list_expenses.html")

@app.route("/update_expense", methods=["GET", "POST"])
def update_expense():
    if "loggedInUser" not in session:
        flash("Autentificare necesara.")
        return redirect(url_for("login"))
    member_id = session["loggedInUser"]
    if request.method == "POST":
        expense_id = request.form.get("expense_id")
        idMembru = member_id
        tip = request.form.get("tip")
        numeCheltuiala = request.form.get("numeCheltuiala")
        cost = request.form.get("cost")
        payload = {
            "idMembru": int(idMembru),
            "tip": tip,
            "numeCheltuiala": numeCheltuiala,
            "cost": float(cost)
        }
        response = kotlin_session.put(f"{KOTLIN_API_BASE}/cheltuieli/actualizeaza/{expense_id}", json=payload, verify=False)
        if response.status_code == 200:
            flash("Cheltuială actualizată cu succes.")
            return redirect(url_for("index"))
        else:
            flash("Eroare: " + response.text)
            return redirect(url_for("update_expense"))
    return render_template("update_expense.html")

@app.route("/delete_expense", methods=["GET", "POST"])
def delete_expense():
    if request.method == "POST":
        expense_id = request.form.get("expense_id")
        response = kotlin_session.delete(f"{KOTLIN_API_BASE}/cheltuieli/sterge/{expense_id}", verify=False)
        if response.status_code == 204:
            flash("Cheltuială ștearsă cu succes.")
            return redirect(url_for("index"))
        else:
            flash("Eroare: " + response.text)
            return redirect(url_for("delete_expense"))
    return render_template("delete_expense.html")

if __name__ == "__main__":
    app.run(debug=True, port=5000)
