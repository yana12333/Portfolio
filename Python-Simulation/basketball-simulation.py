import pygame
import random
import math

pygame.init()

WIDTH = 1400
HEIGHT = 800
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Баскетбол")

fps = 60
clock = pygame.time.Clock()

# Параметри для моделювання
wall_thickness = 5
selected_ball = None
dragging = False
prev_mouse_pos = None
throw_power = 0.3
gravity = 0.5
bounce_stop = 1.5
coef_friction = 0.2
retention = 0.9

score = 0
max_balls = 15
balls = []

font = pygame.font.Font(None, 30)

# Функція, що випадково змінює колір
def set_color():
    r = random.choice([0, 255])
    g = random.choice([0, 255])
    b = random.choice([0, 255])
    if r == 255 and b == 255 and g == 255:
        return (0, 0, 0)
    return (r, g, b)

# Клас, що створює коло - "м'ячі"
class Ball:
    def __init__(self, x_pos, y_pos, x_speed, y_speed):
        self.x_pos = x_pos
        self.y_pos = y_pos
        self.radius = random.randint(30, 50)
        self.x_speed = x_speed
        self.y_speed = y_speed
        self.color = set_color()
        self.prev_y_pos = y_pos
        self.mass = self.radius ** 2 * 0.1
        
    def draw(self, screen):
        pygame.draw.circle(screen, self.color, (self.x_pos, self.y_pos), self.radius)
    
    # Метод реалізації відскоку від "землі"
    def check_gravity(self):
        if self.y_pos + self.y_speed < HEIGHT - self.radius:
            self.y_speed += gravity
        else:
            self.y_pos = HEIGHT - self.radius
            if abs(self.y_speed) > bounce_stop:
                self.y_speed = -self.y_speed * retention
            elif abs(self.y_speed) <= bounce_stop:
                self.y_speed = 0
                if abs(self.x_speed) > coef_friction:
                    self.x_speed -= (coef_friction * gravity) * (1 if self.x_speed > 0 else -1)
                else:
                    self.x_speed = 0
        return self.y_speed
        

    # Оновлення позиції м'яча
    def update_pos(self):
        self.prev_y_pos = self.y_pos
        self.x_pos += self.x_speed
        self.y_pos += self.y_speed

        if self.x_pos - self.radius <= 0:
            self.x_speed = -self.x_speed * retention
            self.x_pos = self.radius

        if self.x_pos + self.radius >= WIDTH:
            self.x_speed = -self.x_speed * retention
            self.x_pos = WIDTH - self.radius

        if self.y_pos - self.radius <= 0:
            self.y_speed = -self.y_speed * retention
            self.y_pos = self.radius

    # Метод, що перевіряє зіткнення з прямокутниками
    def check_collision_with_rect(self, rect):
        nearest_x = max(rect.x_pos, min(self.x_pos, rect.x_pos + rect.width))
        nearest_y = max(rect.y_pos, min(self.y_pos, rect.y_pos + rect.height))
        dx = self.x_pos - nearest_x
        dy = self.y_pos - nearest_y
        distance = math.sqrt(dx ** 2 + dy ** 2)

        if distance <= self.radius:
            if abs(dx) > abs(dy):
                self.x_speed = -self.x_speed * retention
                if dx > 0:
                    self.x_pos = rect.x_pos + rect.width + self.radius
                else:
                    self.x_pos = rect.x_pos - self.radius
            else:
                self.y_speed = -self.y_speed * retention * 0.8
                if abs(self.y_speed) < bounce_stop * 1.4:
                    self.y_speed = 0
                if dy > 0:
                    self.y_pos = rect.y_pos + rect.height + self.radius
                else:
                    self.y_pos = rect.y_pos - self.radius
   
    # Метод, що перевіряє чи був м'яч натиснутий
    def is_clicked(self, mouse_pos):
        dx = self.x_pos - mouse_pos[0]
        dy = self.y_pos - mouse_pos[1]
        distance = math.sqrt(dx**2 + dy**2)
        return distance <= self.radius
    
    # Метод, що перевіряє зіткнення м'ячів
    def check_collision_with_ball(self, other):
        dx = self.x_pos - other.x_pos
        dy = self.y_pos - other.y_pos
        distance = math.sqrt(dx**2 + dy**2)
        if distance < self.radius + other.radius:
            
            # Нормаль до поверхні зіткнення
            nx = dx / distance
            ny = dy / distance

            # Відносна швидкість
            vx1 = self.x_speed
            vy1 = self.y_speed
            vx2 = other.x_speed
            vy2 = other.y_speed

            v1n = vx1 * nx + vy1 * ny
            v2n = vx2 * nx + vy2 * ny

            m1 = self.mass
            m2 = other.mass

            # Оновлення швидкостей вздовж нормалі
            v1n_new = ((m1 - m2) / (m1 + m2)) * v1n + ((2 * m2) / (m1 + m2)) * v2n
            v2n_new = ((m2 - m1) / (m1 + m2)) * v2n + ((2 * m1) / (m1 + m2)) * v1n


            # Оновлення швидкостей м'ячів
            self.x_speed = vx1 + (v1n_new - v1n) * nx
            self.y_speed = vy1 + (v1n_new - v1n) * ny
            other.x_speed = vx2 + (v2n_new - v2n) * nx
            other.y_speed = vy2 + (v2n_new - v2n) * ny

            # Корекція позиції
            overlap = self.radius + other.radius - distance
            correction = overlap / 2
            self.x_pos += correction * nx
            self.y_pos += correction * ny
            other.x_pos -= correction * nx
            other.y_pos -= correction * ny
    
# Функція що перевіряє чи натиснута кнопка
def is_button_clicked(rectangle, mouse_pos):
    return (
        rectangle.x_pos <= mouse_pos[0] <= rectangle.x_pos + rectangle.width and
        rectangle.y_pos <= mouse_pos[1] <= rectangle.y_pos + rectangle.height
    )

# Клас прямокутників
class Rectangle: 
    def __init__(self, x_pos, y_pos, width, height, color):
        self.x_pos = x_pos
        self.y_pos = y_pos
        self.width = width
        self.height = height
        self.color = color

    def draw(self, screen):
        pygame.draw.rect(screen, self.color, (self.x_pos, self.y_pos, self.width, self.height))



# Початковий м'яч
balls.append(Ball(1200, 200, 0, 10))

# Кошик
basket_line = Rectangle(50, 350, 150, 13, "black")
basket_rec = Rectangle(10, 220, 70, 180, "black")
basket_lil = Rectangle(200, 350, 4, 13, "black")

# Елементи кнопки
button = Rectangle(1200, 30, 140, 40, "light blue")
button_line = Rectangle(1200, 30, 142, 42, "black")

button_clear = Rectangle(1190, 120, 162, 33, "orange")
button_line2 = Rectangle(1190, 120, 164, 35, "black")

# Основний цикл
run = True
while run:
    clock.tick(fps)
    screen.fill("white")

    # Кнопка "Додати м'яч"
    button_line.draw(screen)
    button.draw(screen)
    text = font.render("Додати м'яч", True, "black")
    screen.blit(text, (button.x_pos + 8, button.y_pos + 8))

    # Кнопка "Очистити"
    button_line2.draw(screen)
    button_clear.draw(screen)
    text2 = font.render("Перезапустити", True, "black")
    screen.blit(text2, (button_clear.x_pos + 8, button_clear.y_pos + 8))

    # Вивід кількості залишку м’ячів
    balls_left = max_balls - len(balls)
    left_text = font.render(f"Можна додати: {balls_left}", True, "black")
    screen.blit(left_text, (button.x_pos, button.y_pos + 50))


    # Обробка м'ячів
    for i, ball in enumerate(balls):
        ball.draw(screen)
        ball.update_pos()
        ball.y_speed = ball.check_gravity()
        if (
            ball.prev_y_pos < basket_line.y_pos and
            ball.y_pos >= basket_line.y_pos and
            basket_line.x_pos <= ball.x_pos <= basket_line.x_pos + basket_line.width
        ):
           score += 1

        ball.check_collision_with_rect(basket_rec)
        ball.check_collision_with_rect(basket_lil)
        
        for j in range(i + 1, len(balls)):
            other_ball = balls[j]
            ball.check_collision_with_ball(other_ball)


    # Кошик
    basket_line.draw(screen)
    basket_rec.draw(screen)
    basket_lil.draw(screen)
 
    # Виведення рахунку на екран
    score_text = font.render(f"Рахунок: {score}", True, "black")
    screen.blit(score_text, (30, 40))


    # Перетягування м'яча
    if dragging and selected_ball is not None:
        mouse_x, mouse_y = pygame.mouse.get_pos()
        if prev_mouse_pos is not None:
            dx = mouse_x - prev_mouse_pos[0]
            dy = mouse_y - prev_mouse_pos[1]
            selected_ball.x_speed = dx * throw_power
            selected_ball.y_speed = dy * throw_power
        selected_ball.x_pos = mouse_x
        selected_ball.y_pos = mouse_y
        prev_mouse_pos = (mouse_x, mouse_y)

    pygame.display.update()

    # Обробка подій
    for event in pygame.event.get():
        if event.type == pygame.QUIT or (event.type == pygame.KEYDOWN and event.key == pygame.K_ESCAPE):
            run = False
        elif event.type == pygame.MOUSEBUTTONDOWN:
            mouse_pos = pygame.mouse.get_pos()
            if is_button_clicked(button, mouse_pos):
                if len(balls) < max_balls:
                    balls.append(Ball(random.randint(300, 1300), 100, 0, 10))
            if is_button_clicked(button_clear, mouse_pos):
                balls.clear()
                score = 0
            else:
                for ball in balls:
                    if ball.is_clicked(mouse_pos):
                        selected_ball = ball
                        dragging = True
                        break
        elif event.type == pygame.MOUSEBUTTONUP:
            dragging = False
            selected_ball = None
            prev_mouse_pos = None

pygame.quit()