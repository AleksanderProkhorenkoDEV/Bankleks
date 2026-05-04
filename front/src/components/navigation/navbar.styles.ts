import { css } from "lit";

export const navbarStyles = css`
    :host {
        display: block;
        width: 100%;
        height: 10svh;
        min-height: 56px;
        max-height: 72px;
    }

    header {
        height: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 2rem;
        border-bottom: 1px solid #1F2937;
        box-sizing: border-box;
    }

    h1 {
        padding: 0;
        margin: 0;
        font-size: clamp(1rem, 3vw, 1.25rem);
        white-space: nowrap;
    }

    nav {
        display: flex;
        align-items: center;
        gap: 2rem;
    }

    .hamburger {
        display: none;
        flex-direction: column;
        gap: 5px;
        cursor: pointer;
        background: none;
        border: none;
        padding: 0.25rem;
    }

    .hamburger span {
        display: block;
        width: 24px;
        height: 2px;
        background: var(--text-primary);
        border-radius: 2px;
        transition: all 0.25s ease;
    }

    .hamburger.open span:nth-child(1) {
        transform: translateY(7px) rotate(45deg);
    }
    .hamburger.open span:nth-child(2) {
        opacity: 0;
    }
    .hamburger.open span:nth-child(3) {
        transform: translateY(-7px) rotate(-45deg);
    }

    .mobile-menu {
        display: none;
        position: fixed;
        top: 10svh;
        left: 0;
        right: 0;
        background: white;
        border-bottom: 1px solid #1F2937;
        flex-direction: column;
        padding: 1rem 2rem;
        gap: 1.25rem;
        z-index: 99;
        box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    }

    .mobile-menu.open {
        display: flex;
    }

    @media (max-width: 640px) {
        nav {
            display: none;
        }

        .hamburger {
            display: flex;
        }
    }
`;