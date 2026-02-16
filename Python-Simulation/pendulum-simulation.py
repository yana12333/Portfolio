import pygame
import math

pygame.init()
WIDTH, HEIGHT = 1000, 800
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Математичний маятник")

clock = pygame.time.Clock()
fps = 60

# Параметри маятника за замовчуванням
L = 400
g = 0.5
theta = math.radians(0)
omega = 0.0
gamma = 0.001
anchor_x = WIDTH // 2
anchor_y = 200

# Колір фону полів вводу та тексту
input_bg_color = (200, 200, 200)
text_color = (0, 0, 0)
font = pygame.font.Font(None, 30)

# Поле вводу для кута
angle_input_rect = pygame.Rect(30, 40, 100, 30)
angle_input_text = str(theta)
angle_active = False

# Поле вводу для довжини
length_input_rect = pygame.Rect(30, 110, 100, 30)
length_input_text = str(L)
length_active = False

# Кнопка оновлення
update_button_rect = pygame.Rect(20, 160, 120, 40)
button_text = font.render("Оновити", True, text_color)
button_text_rect = button_text.get_rect(center=update_button_rect.center)

# Повідомлення про помилку
error_message = ""
error_time = 0

# Функція перевірки вводу
def is_valid_number(symbol):
    symbol = symbol.strip().replace(',', '.')
    if symbol.count('.') > 1 or symbol.count('-') > 1:
        return False
    if symbol.startswith('-'):
        symbol = symbol[1:]
    return symbol.replace('.', '', 1).isdigit()

# Функція малювання поля вводу
def draw_input_box(rect, text, active):
    color = (150, 150, 150) if active else input_bg_color
    pygame.draw.rect(screen, color, rect, 2)
    text_surface = font.render(text, True, text_color)
    screen.blit(text_surface, (rect.x + 5, rect.y + 5))

# Основний цикл
run = True
while run:
    clock.tick(fps)
    screen.fill("white")

    # Обчислення та оновлення маятника
    alpha = - (g / L) * math.sin(theta)
    omega += alpha
    omega *= (1 - gamma)
    if abs(omega) < 0.000005:
       omega = 0
    theta += omega
    x = anchor_x + L * math.sin(theta)
    y = anchor_y + L * math.cos(theta)

    # Малювання маятника
    pygame.draw.line(screen, "black", (anchor_x, anchor_y), (x, y), 3)
    pygame.draw.circle(screen, "black", (x, y), 22, width=3)
    pygame.draw.circle(screen, "red", (x, y), 20)
    pygame.draw.circle(screen, "black", (anchor_x, anchor_y), 5)

    # Поля вводу
    draw_input_box(angle_input_rect, angle_input_text, angle_active)
    draw_input_box(length_input_rect, length_input_text, length_active)
    screen.blit(font.render("Кут (°)", True, text_color), (35, 15))
    screen.blit(font.render("Довжина (px)", True, text_color), (15, 80))

    # Кнопка оновлення
    pygame.draw.rect(screen, "light blue", update_button_rect)
    pygame.draw.rect(screen, "black", update_button_rect, 2)
    screen.blit(button_text, button_text_rect)

    # Виведення повідомлення про помилку
    if error_message and pygame.time.get_ticks() < error_time:
        error_text = font.render(error_message, True, "red")
        screen.blit(error_text, (20, 220))

    # Обробка миші
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            run = False
        if event.type == pygame.MOUSEBUTTONDOWN:
            if angle_input_rect.collidepoint(event.pos):
                if not angle_active:
                   angle_input_text = ""
                angle_active = True
                length_active = False
            elif length_input_rect.collidepoint(event.pos):
                if not length_active:
                   length_input_text = ""
                length_active = True
                angle_active = False
            else:
                angle_active = False
                length_active = False

            if update_button_rect.collidepoint(event.pos):
                if is_valid_number(angle_input_text) and is_valid_number(length_input_text):
                    new_angle_deg = float(angle_input_text.replace(',', '.'))
                    L = abs(float(length_input_text.replace(',', '.')))
                    theta = math.radians(new_angle_deg)
                    omega = 0.0
                    error_message = ""
                else:
                    error_message = "Некоректний ввід"
                    error_time = pygame.time.get_ticks() + 3000
                    if not is_valid_number(angle_input_text):
                        angle_input_text = ""
                    if not is_valid_number(length_input_text):
                        length_input_text = ""

        # Обробка клавіатури
        if event.type == pygame.KEYDOWN:
            if angle_active:
                if event.key == pygame.K_RETURN:
                    angle_active = False
                elif event.key == pygame.K_BACKSPACE:
                    angle_input_text = angle_input_text[:-1]
                else:
                    angle_input_text += event.unicode
            elif length_active:
                if event.key == pygame.K_RETURN:
                    length_active = False
                elif event.key == pygame.K_BACKSPACE:
                    length_input_text = length_input_text[:-1]
                else:
                    length_input_text += event.unicode

    pygame.display.update()

pygame.quit()
